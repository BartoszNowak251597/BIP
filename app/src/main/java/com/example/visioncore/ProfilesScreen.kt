package com.example.visioncore

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

@Composable
fun ProfilesScreen(
    modifier: Modifier = Modifier,
    profiles: List<Profile>,
    activeProfileId: Int,
    onProfileClick: (Profile) -> Unit,
    onAddProfileClick: (String) -> Unit,
    onDeleteActiveProfileClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var newProfileName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Profiles")

        OutlinedTextField(
            value = newProfileName,
            onValueChange = { value -> newProfileName = value },
            label = { Text(text = "Profile name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onAddProfileClick(newProfileName)
                newProfileName = ""
            }
        ) {
            Text(text = "Add profile")
        }

        Button(onClick = onDeleteActiveProfileClick) {
            Text(text = "Delete active profile")
        }

        Button(onClick = onBackClick) {
            Text(text = "Back")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = profiles,
                key = { profile -> profile.id }
            ) { profile ->
                Button(
                    onClick = { onProfileClick(profile) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (profile.id == activeProfileId) {
                        Text(text = "${profile.name} · ACTIVE")
                    } else {
                        Text(text = profile.name)
                    }
                }
            }
        }
    }
}
