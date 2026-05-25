package com.example.visioncore

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.caverock.androidsvg.SVG
import com.example.visioncore.bluetooth.BleDevice
import com.example.visioncore.bluetooth.BluetoothDisabledException
import com.example.visioncore.bluetooth.BluetoothRepository
import com.example.visioncore.bluetooth.ConnectionState
import kotlinx.coroutines.launch

@Composable
fun BluetoothScreen(
    modifier: Modifier = Modifier,
    bluetoothRepository: BluetoothRepository,
    onBackClick: () -> Unit,
    onConnected: (deviceName: String) -> Unit,
    onContinueClick: () -> Unit
) {
    val connectionState by bluetoothRepository.connectionState.collectAsState()
    val scannedDevices by bluetoothRepository.scannedDevices.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val isSearching = connectionState == ConnectionState.Scanning
    val isConnecting = connectionState == ConnectionState.Connecting
    val isConnected = connectionState == ConnectionState.Connected

    var connectingDevice by remember { mutableStateOf<BleDevice?>(null) }
    var bluetoothError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(connectionState) {
        if (connectionState == ConnectionState.Connected) {
            onConnected(connectingDevice?.name ?: "VisionCore")
        }
    }

    DisposableEffect(Unit) {
        onDispose { bluetoothRepository.stopScan() }
    }

    val requiredPermissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            try {
                bluetoothRepository.startScan()
            } catch (e: BluetoothDisabledException) {
                bluetoothError = "Bluetooth is disabled"
            }
        } else {
            bluetoothError = "Bluetooth permission required"
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "bluetooth-loader")

    val loaderRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "loader-rotation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "◀",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable { onBackClick() }
            )

            Text(
                text = "Pair the glasses",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Press and hold the side button until the LED blinks.",
            fontSize = 13.sp,
            lineHeight = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(80.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SvgImage(
                svgCode = phoneSvg,
                width = 32.dp,
                height = 32.dp,
                contentDescription = "Phone"
            )

            Spacer(modifier = Modifier.width(8.dp))

            SvgImage(
                svgCode = dashedLineSvg,
                width = 64.dp,
                height = 6.dp,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(6.dp))

            when {
                isConnected || scannedDevices.isNotEmpty() -> {
                    SvgImage(
                        svgCode = checkSvg,
                        width = 26.dp,
                        height = 26.dp,
                        contentDescription = "Device found"
                    )
                }

                isSearching || isConnecting -> {
                    SvgImage(
                        svgCode = loaderSvg,
                        width = 24.dp,
                        height = 24.dp,
                        contentDescription = "Searching",
                        modifier = Modifier.rotate(loaderRotation)
                    )
                }

                else -> {
                    SvgImage(
                        svgCode = dashedLineSvg,
                        width = 34.dp,
                        height = 6.dp,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            SvgImage(
                svgCode = visionCoreLogoSvg,
                width = 68.dp,
                height = 60.dp,
                contentDescription = "VisionCore logo"
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 78.dp, max = 220.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (scannedDevices.isEmpty()) {
                item {
                    EmptyDeviceCard(
                        isSearching = isSearching || isConnecting
                    )
                }
            }

            items(
                items = scannedDevices,
                key = { device -> device.address }
            ) { device ->
                DeviceCard(
                    device = device,
                    connected = isConnected && connectingDevice?.address == device.address,
                    connecting = isConnecting && connectingDevice?.address == device.address,
                    onConnectClick = {
                        scope.launch {
                            connectingDevice = device
                            try {
                                bluetoothRepository.connect(device)
                            } catch (e: BluetoothDisabledException) {
                                bluetoothError = "Bluetooth is disabled"
                                connectingDevice = null
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                if (isSearching) {
                    bluetoothRepository.stopScan()
                } else {
                    bluetoothError = null
                    connectingDevice = null
                    val hasPermissions = requiredPermissions.all {
                        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                    }
                    if (hasPermissions) {
                        try {
                            bluetoothRepository.startScan()
                        } catch (e: BluetoothDisabledException) {
                            bluetoothError = "Bluetooth is disabled"
                        }
                    } else {
                        permissionLauncher.launch(requiredPermissions)
                    }
                }
            },
            enabled = !isConnected && !isConnecting,
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .height(44.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.Black.copy(alpha = 0.35f),
                disabledContentColor = Color.White
            )
        ) {
            Text(
                text = if (isSearching) "Stop search" else "Search",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                isConnected -> "Connected"
                isConnecting -> "Connecting..."
                scannedDevices.isNotEmpty() -> "Device found"
                isSearching -> "Searching"
                bluetoothError != null -> bluetoothError!!
                else -> "Ready to search"
            },
            fontSize = 14.sp,
            color = if (bluetoothError != null && !isConnected && !isSearching && !isConnecting) {
                Color.Red
            } else {
                Color.Black
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { if (isConnected) onContinueClick() },
            enabled = isConnected,
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .height(48.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.Black.copy(alpha = 0.35f),
                disabledContentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 2.dp
            )
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(28.dp))

            Text(
                text = "▶",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyDeviceCard(
    isSearching: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isSearching) "Searching for VisionCore..." else "No device selected",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = if (isSearching) "Make sure LED is blinking" else "Press Search to find glasses",
                fontSize = 11.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun DeviceCard(
    device: BleDevice,
    connected: Boolean,
    connecting: Boolean,
    onConnectClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = when {
                        connected -> "Connected"
                        connecting -> "Connecting..."
                        else -> "Nearby ${device.rssi} dBm"
                    },
                    fontSize = 11.sp,
                    color = Color.Black
                )
            }

            when {
                connected -> {
                    Text(
                        text = "connected",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                connecting -> {
                    Text(
                        text = "connecting...",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                else -> {
                    Button(
                        onClick = onConnectClick,
                        modifier = Modifier
                            .height(32.dp)
                            .clip(CircleShape),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "connect",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SvgImage(
    svgCode: String,
    width: Dp,
    height: Dp,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val widthPx = with(density) { width.roundToPx() }.coerceAtLeast(1)
    val heightPx = with(density) { height.roundToPx() }.coerceAtLeast(1)

    val imageBitmap = remember(svgCode, widthPx, heightPx) {
        try {
            val svg = SVG.getFromString(svgCode)
            val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            svg.setDocumentWidth(widthPx.toFloat())
            svg.setDocumentHeight(heightPx.toFloat())
            svg.renderToCanvas(canvas)

            bitmap.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    imageBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier.size(width, height),
            contentScale = ContentScale.Fit
        )
    }
}

private val phoneSvg = """
<svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
<path d="M20 30H20.0167M11.6667 3.33333H28.3333C30.1743 3.33333 31.6667 4.82571 31.6667 6.66666V33.3333C31.6667 35.1743 30.1743 36.6667 28.3333 36.6667H11.6667C9.82572 36.6667 8.33333 35.1743 8.33333 33.3333V6.66666C8.33333 4.82571 9.82572 3.33333 11.6667 3.33333Z" stroke="#1E1E1E" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
</svg>
""".trimIndent()

private val loaderSvg = """
<svg width="32" height="32" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
<g clip-path="url(#clip0_6_288)">
<path d="M16 2.66666V7.99999M16 24V29.3333M6.57333 6.57332L10.3467 10.3467M21.6533 21.6533L25.4267 25.4267M2.66666 16H8M24 16H29.3333M6.57333 25.4267L10.3467 21.6533M21.6533 10.3467L25.4267 6.57332" stroke="#1E1E1E" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
</g>
<defs>
<clipPath id="clip0_6_288">
<rect width="32" height="32" fill="white"/>
</clipPath>
</defs>
</svg>
""".trimIndent()

private val dashedLineSvg = """
<svg width="60" height="3" viewBox="0 0 60 3" fill="none" xmlns="http://www.w3.org/2000/svg">
<path d="M0 1.5H60" stroke="black" stroke-width="3" stroke-dasharray="6 6"/>
</svg>
""".trimIndent()

private val checkSvg = """
<svg width="32" height="32" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
<circle cx="16" cy="16" r="14" fill="#171717"/>
<path d="M9.5 16.5L14 21L23 11.5" stroke="white" stroke-width="3.2" stroke-linecap="round" stroke-linejoin="round"/>
</svg>
""".trimIndent()

private val visionCoreLogoSvg = """
<svg width="68" height="60" viewBox="0 0 68 60" fill="none" xmlns="http://www.w3.org/2000/svg">
<g filter="url(#filter0_d_6_325)">
<g clip-path="url(#clip0_6_325)">
<path fill-rule="evenodd" clip-rule="evenodd" d="M63.6518 27.4841L48.582 24.1141L59.6313 34.5557C53.7289 40.1025 43.9972 39.9242 37.8949 34.1575C31.7968 28.3945 31.6345 19.2155 37.5329 13.6725C43.4313 8.12945 53.1711 8.30793 59.2692 14.0708C63.0338 17.6283 64.6854 22.6834 63.6518 27.4841Z" fill="#FF1919"/>
<path fill-rule="evenodd" clip-rule="evenodd" d="M27.5657 9.62914L32.9343 38.4915L4 28.7457L27.5657 9.62914Z" fill="#171717"/>
<path d="M48.5838 29.0617C51.466 29.0617 53.8024 26.8466 53.8024 24.1141C53.8024 21.3816 51.466 19.1665 48.5838 19.1665C45.7016 19.1665 43.3651 21.3816 43.3651 24.1141C43.3651 26.8466 45.7016 29.0617 48.5838 29.0617Z" fill="#272727"/>
<path d="M6.79878 42.7855L9.93949 50.1595H11.0937L14.2344 42.7855H12.3922L10.5167 47.5062L8.64112 42.7855H6.79878Z" fill="black"/>
<path d="M15.0112 45.2287V50.1595H16.6092V45.2287H15.0112ZM14.878 43.1282C14.878 43.6478 15.2886 44.0568 15.8102 44.0568C16.3318 44.0568 16.7423 43.6478 16.7423 43.1282C16.7423 42.6086 16.3317 42.1996 15.8102 42.1996C15.2885 42.1996 14.878 42.6086 14.878 43.1282Z" fill="black"/>
<path d="M21.6254 45.483C21.2259 45.2508 20.7153 45.0629 19.983 45.0629C19.5279 45.0629 18.9286 45.1514 18.4848 45.5604C18.1963 45.8258 18.0076 46.2237 18.0076 46.6991C18.0076 47.0749 18.1297 47.3514 18.3627 47.5946C18.5736 47.8047 18.8732 47.9595 19.1618 48.0479L19.5613 48.1695C19.7944 48.2358 19.9387 48.2801 20.0497 48.3464C20.1939 48.4348 20.2272 48.5454 20.2272 48.6338C20.2272 48.7554 20.1606 48.877 20.0607 48.9544C19.9165 49.065 19.6612 49.065 19.5613 49.065C19.3505 49.065 19.1174 49.0207 18.8954 48.9101C18.7289 48.8328 18.507 48.678 18.3517 48.5453L17.6747 49.6176C18.3184 50.1815 19.0397 50.3252 19.7056 50.3252C20.2272 50.3252 20.8265 50.2478 21.3147 49.7613C21.5367 49.5402 21.8252 49.1201 21.8252 48.4678C21.8252 48.092 21.7253 47.7934 21.4368 47.5281C21.1815 47.2959 20.893 47.1854 20.6155 47.0969L20.1938 46.9642C19.994 46.898 19.8276 46.8648 19.7167 46.7985C19.639 46.7542 19.5613 46.6879 19.5613 46.5773C19.5613 46.5 19.6057 46.4116 19.6612 46.3562C19.7611 46.2567 19.9498 46.2125 20.1163 46.2125C20.427 46.2125 20.7488 46.3452 20.993 46.4888L21.6256 45.4827L21.6254 45.483Z" fill="black"/>
<path d="M22.8906 45.2287V50.1595H24.4886V45.2287H22.8906ZM22.7574 43.1282C22.7574 43.6478 23.168 44.0568 23.6895 44.0568C24.2112 44.0568 24.6217 43.6478 24.6217 43.1282C24.6217 42.6086 24.2111 42.1996 23.6895 42.1996C23.1679 42.1996 22.7574 42.6086 22.7574 43.1282Z" fill="black"/>
<path d="M31.4136 47.6941C31.4136 47.064 31.1694 46.3564 30.67 45.8589C30.2261 45.4166 29.4715 45.0628 28.506 45.0628C27.5405 45.0628 26.7859 45.4166 26.3419 45.8589C25.8425 46.3563 25.5984 47.064 25.5984 47.6941C25.5984 48.3243 25.8425 49.0318 26.3419 49.5293C26.7859 49.9716 27.5405 50.3254 28.506 50.3254C29.4715 50.3254 30.2261 49.9716 30.67 49.5293C31.1694 49.0319 31.4136 48.3243 31.4136 47.6941ZM28.506 46.4117C28.8722 46.4117 29.1719 46.5333 29.4049 46.7655C29.638 46.9977 29.7711 47.2961 29.7711 47.6941C29.7711 48.0921 29.6379 48.3906 29.4049 48.6227C29.1718 48.8549 28.8722 48.9765 28.5171 48.9765C28.0953 48.9765 27.8068 48.8217 27.607 48.6227C27.4184 48.4348 27.2408 48.1473 27.2408 47.6941C27.2408 47.2961 27.374 46.9977 27.607 46.7655C27.8401 46.5333 28.1397 46.4117 28.5059 46.4117H28.506Z" fill="black"/>
<path d="M32.4124 50.1595H34.0105V47.6278C34.0105 47.3072 34.0548 46.9534 34.3212 46.6881C34.4432 46.5554 34.6541 46.4227 34.9871 46.4227C35.2756 46.4227 35.4754 46.5222 35.5975 46.6438C35.8527 46.8982 35.8639 47.3072 35.8639 47.6278V50.1595H37.4619V47.0418C37.4619 46.6438 37.4286 46.0579 36.9625 45.5936C36.5408 45.1735 35.9748 45.0961 35.542 45.0961C35.0759 45.0961 34.4989 45.1956 34.0106 45.8147V45.2287H32.4125V50.1595H32.4124Z" fill="black"/>
<path d="M44.2426 47.9706C43.5879 48.6118 42.9886 48.7334 42.5447 48.7334C41.6346 48.7334 41.1019 48.269 40.9466 48.1143C40.6137 47.7937 40.3029 47.252 40.3029 46.4891C40.3029 45.7816 40.5915 45.1846 41.0021 44.7866C41.3906 44.4108 41.89 44.2117 42.5115 44.2117C43.2994 44.2117 43.8654 44.5986 44.2427 44.9966V43.0287C43.5214 42.7192 42.9554 42.6197 42.4671 42.6197C41.3129 42.6197 40.3586 43.0509 39.7038 43.681C39.0379 44.3222 38.5495 45.2951 38.5495 46.4559C38.5495 47.6389 39.0379 48.6118 39.7038 49.253C40.3253 49.861 41.2463 50.3254 42.6114 50.3254C43.0553 50.3254 43.588 50.2591 44.2427 49.9274L44.2426 47.9706Z" fill="#FF1919"/>
<path d="M50.99 47.6941C50.99 47.064 50.7459 46.3564 50.2465 45.8589C49.8025 45.4166 49.0479 45.0628 48.0824 45.0628C47.1169 45.0628 46.3623 45.4166 45.9184 45.8589C45.419 46.3563 45.1748 47.064 45.1748 47.6941C45.1748 48.3243 45.419 49.0318 45.9184 49.5293C46.3623 49.9716 47.1169 50.3254 48.0824 50.3254C49.0479 50.3254 49.8025 49.9716 50.2465 49.5293C50.7459 49.0319 50.99 48.3243 50.99 47.6941ZM48.0824 46.4117C48.4486 46.4117 48.7483 46.5333 48.9813 46.7655C49.2144 46.9977 49.3475 47.2961 49.3475 47.6941C49.3475 48.0921 49.2143 48.3906 48.9813 48.6227C48.7482 48.8549 48.4486 48.9765 48.0935 48.9765C47.6718 48.9765 47.3832 48.8217 47.1834 48.6227C46.9948 48.4348 46.8172 48.1473 46.8172 47.6941C46.8172 47.2961 46.9504 46.9977 47.1834 46.7655C47.4165 46.5333 47.7161 46.4117 48.0823 46.4117H48.0824Z" fill="#171717"/>
<path d="M51.9888 50.1595H53.5869V48.1585C53.5869 47.7384 53.6202 47.2077 53.9753 46.8318C54.1751 46.6217 54.4304 46.5001 54.7744 46.5001C55.0519 46.5001 55.2849 46.5664 55.4957 46.6769L55.5846 45.1844C55.407 45.1292 55.2627 45.1071 55.0629 45.1071C54.7522 45.1071 54.4747 45.1514 54.2084 45.3171C53.9532 45.472 53.7313 45.7262 53.5869 46.0026V45.2287H51.9888V50.1595Z" fill="#FF1919"/>
<path d="M57.5155 47.0198C57.5599 46.7987 57.6709 46.6218 57.793 46.4891C57.9483 46.3343 58.1925 46.1685 58.6032 46.1685C58.925 46.1685 59.1802 46.2791 59.3578 46.4449C59.5909 46.666 59.6685 46.9755 59.6796 47.0198H57.5155ZM61.1556 47.8048C61.1556 47.1968 61.0447 46.4228 60.4565 45.8038C59.8461 45.1625 59.047 45.063 58.4922 45.063C57.9373 45.063 57.1937 45.1736 56.5945 45.7706C56.0951 46.2791 55.8399 47.0087 55.8399 47.7163C55.8399 48.5344 56.1507 49.1867 56.5835 49.6179C57.1605 50.1928 57.8153 50.3254 58.5589 50.3254C59.1138 50.3254 59.6465 50.248 60.1237 49.9605C60.4122 49.7837 60.8561 49.4188 61.0781 48.7223L59.5687 48.5675C59.4355 48.8439 59.2469 48.9655 59.2247 48.9766C59.0582 49.076 58.814 49.1203 58.581 49.1203C58.3369 49.1203 58.004 49.076 57.7598 48.8108C57.56 48.5896 57.4602 48.2358 57.4602 47.9706H61.1557V47.8048H61.1556Z" fill="#FF1919"/>
</g>
</g>
<defs>
<filter id="filter0_d_6_325" x="0" y="0" width="68" height="68" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
<feFlood flood-opacity="0" result="BackgroundImageFix"/>
<feColorMatrix in="SourceAlpha" type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"/>
<feOffset dy="4"/>
<feGaussianBlur stdDeviation="2"/>
<feComposite in2="hardAlpha" operator="out"/>
<feColorMatrix type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.25 0"/>
<feBlend mode="normal" in2="BackgroundImageFix" result="effect1_dropShadow_6_325"/>
<feBlend mode="normal" in="SourceGraphic" in2="effect1_dropShadow_6_325" result="shape"/>
</filter>
<clipPath id="clip0_6_325">
<rect width="60" height="60" fill="white" transform="translate(4)"/>
</clipPath>
</defs>
</svg>
""".trimIndent()
