package com.example.visioncore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileDetailsScreen(
    modifier: Modifier = Modifier,
    profile: Profile,
    onDeleteProfileClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = profile.name)

        Button(onClick = onDeleteProfileClick) {
            Text(text = "Delete profile")
        }

        Text(text = "Created at: ${profile.createdAt}")

        Text(text = "Diopters")
        Text(text = "Near left: ${profile.nearLeft}")
        Text(text = "Near right: ${profile.nearRight}")
        Text(text = "Far left: ${profile.farLeft}")
        Text(text = "Far right: ${profile.farRight}")

        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Back")
        }
    }
}
