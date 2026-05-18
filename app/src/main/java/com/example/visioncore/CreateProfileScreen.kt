package com.example.visioncore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun CreateProfileScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onAddProfile: (String, String) -> Unit // name + ageRange
) {
    var name by remember { mutableStateOf("Jacob") }
    var selectedAge by remember { mutableStateOf("<18") }

    val ageOptions = listOf("<18", "18-40", "40-60", "+60")

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {

            // HEADER
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Text(
                        text = "◀",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Profile",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Who is this profile for ?", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Text("profile name", fontSize = 14.sp, color = Color.Gray)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("age range", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ageOptions.forEach { age ->

                    val selected = selectedAge == age

                    OutlinedButton(
                        onClick = { selectedAge = age },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(2.dp, Color.Black),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selected) Color.Black else Color.White,
                            contentColor = if (selected) Color.White else Color.Black
                        )
                    ) {
                        Text(age)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ADD PROFILE BUTTON (jak na mocku)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        onAddProfile(name, selectedAge)
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(52.dp)
                ) {
                    Text("add profile", color = Color.White, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("▶", color = Color.White)
                }
            }
        }
    }
}