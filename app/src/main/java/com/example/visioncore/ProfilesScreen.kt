package com.example.visioncore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ProfilesScreen(
    modifier: Modifier = Modifier,
    profiles: List<Profile>,
    activeProfileId: Int,
    onProfileClick: (Profile) -> Unit,
    onAddProfileClick: () -> Unit,
    onEditProfileClick: (Profile, String) -> Unit,
    onDeleteProfileClick: (Profile) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    var selectedProfileId by remember(activeProfileId) { mutableIntStateOf(activeProfileId) }
    var profileToEdit by remember { mutableStateOf<Profile?>(null) }
    var editedName by remember { mutableStateOf("") }
    var editedAgeRange by remember { mutableStateOf("") }

    val selectedProfile = profiles.firstOrNull { it.id == selectedProfileId } ?: profiles.firstOrNull()
    val editingProfile = profileToEdit

    if (editingProfile != null) {
        Dialog(
            onDismissRequest = {
                profileToEdit = null
                editedName = ""
                editedAgeRange = ""
            }
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "profile name",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Text(
                        text = "age range",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AgeRangeButton(
                            text = "<18",
                            selected = editedAgeRange == "<18",
                            onClick = { editedAgeRange = "<18" },
                            modifier = Modifier.weight(1f)
                        )

                        AgeRangeButton(
                            text = "18-40",
                            selected = editedAgeRange == "18-40",
                            onClick = { editedAgeRange = "18-40" },
                            modifier = Modifier.weight(1.25f)
                        )

                        AgeRangeButton(
                            text = "40-60",
                            selected = editedAgeRange == "40-60",
                            onClick = { editedAgeRange = "40-60" },
                            modifier = Modifier.weight(1.25f)
                        )

                        AgeRangeButton(
                            text = "+60",
                            selected = editedAgeRange == "+60",
                            onClick = { editedAgeRange = "+60" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                profileToEdit = null
                                editedName = ""
                                editedAgeRange = ""
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(2.dp, Color.Black),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                val finalProfileName = buildProfileNameWithAgeRange(
                                    name = editedName,
                                    ageRange = editedAgeRange,
                                    fallbackName = editingProfile.name
                                )

                                onEditProfileClick(editingProfile, finalProfileName)
                                selectedProfileId = editingProfile.id
                                profileToEdit = null
                                editedName = ""
                                editedAgeRange = ""
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Save",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

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
                        text = "◀ ",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Profiles",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Profiles store each user’s personalized glasses settings.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onAddProfileClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                border = BorderStroke(2.dp, Color.Black),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Create new profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(profiles, key = { it.id }) { profile ->
                    val isSelected = profile.id == selectedProfileId
                    val isActive = profile.id == activeProfileId

                    Surface(
                        onClick = {
                            selectedProfileId = profile.id
                            onProfileClick(profile)
                        },
                        shape = RoundedCornerShape(50),
                        color = if (isSelected) Color.Black else Color(0xFFF2F2F2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else Color.Black
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = profile.name,
                                color = if (isSelected) Color.White else Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            if (isActive) {
                                Text(
                                    text = "ACTIVE",
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedProfile?.let { profile ->
                            profileToEdit = profile
                            editedName = extractProfileNameWithoutAgeRange(profile.name)
                            editedAgeRange = extractAgeRangeFromProfileName(profile.name)
                        }
                    },
                    enabled = selectedProfile != null,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(2.dp, Color.Black),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Edit",
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        selectedProfile?.let { profile ->
                            onDeleteProfileClick(profile)
                            val nextProfile = profiles.firstOrNull { it.id != profile.id }
                            if (nextProfile != null) {
                                selectedProfileId = nextProfile.id
                                onProfileClick(nextProfile)
                            }
                        }
                    },
                    enabled = selectedProfile != null && profiles.size > 1,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(2.dp, Color.Black),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Delete",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onContinueClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(52.dp)
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "▶",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun AgeRangeButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(50),
        border = BorderStroke(2.dp, Color.Black),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color.Black else Color.White,
            contentColor = if (selected) Color.White else Color.Black
        ),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun extractProfileNameWithoutAgeRange(profileName: String): String {
    return profileName.replace(Regex("\\s*\\([^)]*\\)\\s*$"), "").trim()
}

private fun extractAgeRangeFromProfileName(profileName: String): String {
    val match = Regex("\\(([^)]*)\\)\\s*$").find(profileName)
    return match?.groupValues?.getOrNull(1)?.trim().orEmpty()
}

private fun buildProfileNameWithAgeRange(
    name: String,
    ageRange: String,
    fallbackName: String
): String {
    val cleanedName = name.trim().ifBlank {
        extractProfileNameWithoutAgeRange(fallbackName).ifBlank { fallbackName }
    }
    val cleanedAgeRange = ageRange.trim()

    return if (cleanedAgeRange.isBlank()) {
        cleanedName
    } else {
        "$cleanedName ($cleanedAgeRange)"
    }
}
