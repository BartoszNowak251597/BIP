package com.example.visioncore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreateProfileScreen(
    modifier: Modifier = Modifier,
    initialName: String = "",
    initialAge: String = "",
    title: String = "Profile",
    description: String = "Who is this profile for?",
    buttonText: String = "add profile",
    onBackClick: () -> Unit,
    onAddProfile: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedAge by remember { mutableStateOf(initialAge) }

    LaunchedEffect(initialName, initialAge) {
        name = initialName
        selectedAge = initialAge
    }

    val ageOptions = listOf("<18", "18-40", "40-60", "+60")
    val canContinue = name.trim().isNotBlank() && selectedAge.isNotBlank()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .navigationBarsPadding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    text = title,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = description,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "profile name",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = {
                        Text(text = "Enter name")
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "age range",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ageOptions.forEach { age ->
                        val selected = selectedAge == age

                        OutlinedButton(
                            onClick = {
                                selectedAge = age
                            },
                            modifier = Modifier
                                .weight(
                                    when (age) {
                                        "<18", "+60" -> 1f
                                        else -> 1.25f
                                    }
                                )
                                .height(42.dp),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(2.dp, Color.Black),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selected) Color.Black else Color.White,
                                contentColor = if (selected) Color.White else Color.Black
                            )
                        ) {
                            Text(
                                text = age,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        onAddProfile(name.trim(), selectedAge)
                    },
                    enabled = canContinue,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE0E0E0),
                        disabledContentColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(54.dp)
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "▶",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}