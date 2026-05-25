package com.example.visioncore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileDetailsScreen(
    modifier: Modifier = Modifier,
    profile: Profile,
    onDeleteProfileClick: () -> Unit,
    onBackClick: () -> Unit
) {

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {

            // HEADER (jak w Profiles)
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
                    text = "Details",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // PROFILE INFO
            Text(
                text = profile.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Created at: ${profile.createdAt}")

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Diopters", fontWeight = FontWeight.Bold)

            Text(text = "Near left: ${profile.nearLeft}")
            Text(text = "Near right: ${profile.nearRight}")
            Text(text = "Far left: ${profile.farLeft}")
            Text(text = "Far right: ${profile.farRight}")

            Spacer(modifier = Modifier.weight(1f))

            // DELETE BUTTON (opcjonalnie zostawiam, ale spójny styl)
            Button(
                onClick = onDeleteProfileClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(52.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Delete profile",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BACK BUTTON jak "Continue"
            Button(
                onClick = onBackClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(52.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Back",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "◀",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}