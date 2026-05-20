#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

BLEServer *pServer = NULL;
BLECharacteristic *pCommandCharacteristic;
BLECharacteristic *pStateCharacteristic;
bool deviceConnected = false;

// Twoje własne identyfikatory UUID (skonwertowane na małe litery dla standardu BLE)
#define SERVICE_UUID "ba89a69d-5c63-4e5b-87e6-3b4d42273fe5"
#define COMMAND_UUID "aad23908-258f-4d17-83b5-e7a6448a022e"
#define STATE_UUID   "fff1224f-4294-410f-b8c8-99e0c67ced6e"

class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
      deviceConnected = true;
      Serial.println("Połączono z urządzeniem!");
    };

    void onDisconnect(BLEServer* pServer) {
      deviceConnected = false;
      Serial.println("Rozłączono. Ponowne uruchamianie rozgłaszania...");
      pServer->getAdvertising()->start(); 
    }
};

// Klasa obsługująca odbieranie danych (zapis do COMMAND_UUID)
class CommandCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *pCharacteristic) {
      String value = pCharacteristic->getValue();

      if (value.length() == 0) return;

      uint8_t cmd = (uint8_t)value[0];

      if (cmd == 0x01 && value.length() == 5) {
        // SET_PRESCRIPTION: 4 dioptre values encoded as int8 * 4
        float nearLeft  = (int8_t)value[1] / 4.0f;
        float nearRight = (int8_t)value[2] / 4.0f;
        float farLeft   = (int8_t)value[3] / 4.0f;
        float farRight  = (int8_t)value[4] / 4.0f;

        Serial.print("Prescription — NL: "); Serial.print(nearLeft, 2);
        Serial.print("  NR: ");              Serial.print(nearRight, 2);
        Serial.print("  FL: ");              Serial.print(farLeft, 2);
        Serial.print("  FR: ");              Serial.println(farRight, 2);

        // TODO: pass values to the lens controller here

      } else if (cmd == 0x02 && value.length() == 2) {
        // SET_MODE: 0x00 = auto (tilt-based), 0x01 = manual (hold position)
        uint8_t mode = (uint8_t)value[1];
        if (mode == 0x00) {
          Serial.println("Mode: auto");
        } else {
          Serial.println("Mode: manual");
        }
        // TODO: apply mode to the lens controller here

      } else if (cmd == 0x03 && value.length() == 2) {
        // SET_DEAD_BATTERY_MODE: 0x00=last, 0x01=near, 0x02=far, 0x03=neutral
        uint8_t mode = (uint8_t)value[1];
        const char* label = (mode == 0x01) ? "lock near"  :
                            (mode == 0x02) ? "lock far"   :
                            (mode == 0x03) ? "neutral 0D" : "stay last";
        Serial.print("Dead battery mode: ");
        Serial.println(label);
        // TODO: persist to flash here

      } else if (cmd == 0x04 && value.length() == 2) {
        uint8_t step = (uint8_t)value[1];

        if (step == 0x00) {
          // Capture far/straight-ahead reference
          // TODO: progGlowaWysoko = readIMU_angleY();
          Serial.println("Calib: far reference captured");
        } else if (step == 0x01) {
          // Capture near/tilt-down reference
          // TODO: progGlowaNisko = readIMU_angleY();
          Serial.println("Calib: near reference captured");
        } else if (step == 0x02) {
          // All steps done — compute threshold
          // TODO: threshold = (progGlowaWysoko + progGlowaNisko) / 2.0f;
          Serial.println("Calib: threshold computed");
        }

        // Respond OK — phone is waiting within 3 seconds
        uint8_t response[2] = { 0x04, 0x01 };
        pStateCharacteristic->setValue(response, 2);
        pStateCharacteristic->notify();

      } else if (cmd == 0x05 && value.length() == 2) {
        // SET_LENS_POSITION: 0x00 = near, 0x01 = far, 0x02 = off
        uint8_t pos = (uint8_t)value[1];
        const char* label = (pos == 0x00) ? "near" :
                            (pos == 0x01) ? "far"  : "off";
        Serial.print("Lens position: ");
        Serial.println(label);
        // TODO: apply lens position here

      } else {
        Serial.print("Unknown command 0x");
        Serial.print(cmd, HEX);
        Serial.print(", length: ");
        Serial.println(value.length());
      }
    }
};

void setup() {
  Serial.begin(115200);
  while (!Serial);

  Serial.println("Uruchamianie niestandardowego serwera BLE...");

  // Nazwa urządzenia widoczna podczas skanowania
  BLEDevice::init("VisionCore");

  pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  // Tworzenie usługi głównej z Twoim UUID
  BLEService *pService = pServer->createService(SERVICE_UUID);

  // Tworzenie charakterystyki COMMAND (Zapis/Write z telefonu/laptopa)
  pCommandCharacteristic = pService->createCharacteristic(
                             COMMAND_UUID,
                             BLECharacteristic::PROPERTY_WRITE
                           );
  pCommandCharacteristic->setCallbacks(new CommandCallbacks());

  // Tworzenie charakterystyki STATE (Odczyt/Read i Powiadomienia/Notify dla telefonu/laptopa)
  pStateCharacteristic = pService->createCharacteristic(
                           STATE_UUID,
                           BLECharacteristic::PROPERTY_READ |
                           BLECharacteristic::PROPERTY_NOTIFY
                         );
  pStateCharacteristic->addDescriptor(new BLE2902());

  // Ustawienie początkowej wartości dla stanu
  pStateCharacteristic->setValue("IDLE");

  // Start usługi
  pService->start();

  // Konfiguracja rozgłaszania (Advertising) - KLUCZOWE do wykrycia
  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->addServiceUUID(SERVICE_UUID); // Windows/Aplikacje filtrują po tym UUID usługi!
  pAdvertising->setScanResponse(true);
  pAdvertising->setMinPreferred(0x06);  
  pAdvertising->setMinPreferred(0x12);
  
  pServer->getAdvertising()->start();
  Serial.println("Serwer BLE działa i rozgłasza Twoje UUID!");
}

void loop() {
  // Przykładowe wysyłanie zmian stanu (STATE) co 5 sekund, jeśli ktoś jest połączony
  if (deviceConnected) {
    static unsigned long lastUpdate = 0;
    if (millis() - lastUpdate > 5000) {
      lastUpdate = millis();
      
      // Zmień wartość stanu i powiadom klienta
      pStateCharacteristic->setValue("RUNNING");
      pStateCharacteristic->notify();
      Serial.println("Wysłano aktualizację stanu: RUNNING");
    }
  }
}