package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.ui.theme.ZingraBorder
import com.example.ui.theme.ZingraCyan
import com.example.ui.theme.ZingraGreen
import com.example.ui.theme.ZingraIndigo
import com.example.ui.theme.ZingraSurface
import com.example.ui.theme.ZingraSurfaceVariant
import com.example.ui.theme.ZingraTextMuted
import com.example.ui.theme.ZingraTextSecondary

@Composable
fun EditProfileDialog(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (displayName: String, username: String, bio: String, avatarColorHex: Long, isVerified: Boolean) -> Unit
) {
    var displayName by remember { mutableStateOf(currentProfile.displayName) }
    var rawUsername by remember {
        mutableStateOf(currentProfile.username.removePrefix("@"))
    }
    var bio by remember { mutableStateOf(currentProfile.bio) }
    var selectedColor by remember { mutableLongStateOf(currentProfile.avatarColorHex) }
    var isVerified by remember { mutableStateOf(currentProfile.isVerifiedCreator) }

    val colorOptions = listOf(
        0xFF38BDF8, // Cyan
        0xFF818CF8, // Indigo
        0xFF10B981, // Emerald Green
        0xFFF59E0B, // Amber
        0xFFF43F5E, // Rose Red
        0xFFA855F7  // Purple
    )

    // Clean username: remove spaces and disallowed characters
    val sanitizedUsername = rawUsername.trim().replace("\\s+".toRegex(), "_")
    val formattedHandle = "@" + sanitizedUsername.ifEmpty { "user" }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Edit Profile",
                    tint = ZingraCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Zingra Handle & Profile",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Live Avatar & Handle Preview
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ZingraSurfaceVariant, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(selectedColor)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName.trim().take(1).uppercase().ifEmpty { "Z" },
                            color = Color.Black,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = displayName.ifEmpty { "Your Name" },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            if (isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Badge",
                                    tint = ZingraCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = formattedHandle,
                            color = ZingraCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display Name Input
                Text(
                    text = "Display Name",
                    fontSize = 12.sp,
                    color = ZingraTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    singleLine = true,
                    placeholder = { Text("e.g. Alex Hunter", color = ZingraTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_display_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ZingraCyan,
                        unfocusedBorderColor = ZingraBorder,
                        focusedContainerColor = ZingraSurface,
                        unfocusedContainerColor = ZingraSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Unique Username Handle Input
                Text(
                    text = "Unique Handle (@username)",
                    fontSize = 12.sp,
                    color = ZingraTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = rawUsername,
                    onValueChange = { rawUsername = it.filter { char -> char.isLetterOrDigit() || char == '_' } },
                    singleLine = true,
                    prefix = { Text("@", color = ZingraCyan, fontWeight = FontWeight.Bold) },
                    placeholder = { Text("unique_handle", color = ZingraTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_username_handle"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ZingraCyan,
                        unfocusedBorderColor = ZingraBorder,
                        focusedContainerColor = ZingraSurface,
                        unfocusedContainerColor = ZingraSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    supportingText = {
                        Text(
                            text = "Free handle customization anytime. Share as zingra.app/$formattedHandle",
                            fontSize = 11.sp,
                            color = ZingraGreen
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Bio
                Text(
                    text = "Bio",
                    fontSize = 12.sp,
                    color = ZingraTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { if (it.length <= 150) bio = it },
                    maxLines = 3,
                    placeholder = { Text("Tell the community what you create...", color = ZingraTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_bio"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ZingraCyan,
                        unfocusedBorderColor = ZingraBorder,
                        focusedContainerColor = ZingraSurface,
                        unfocusedContainerColor = ZingraSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Avatar Accent Color Palette
                Text(
                    text = "Avatar Color Theme",
                    fontSize = 12.sp,
                    color = ZingraTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    colorOptions.forEach { colorHex ->
                        val isSelected = selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .clickable { selectedColor = colorHex }
                                .then(
                                    if (isSelected) Modifier.border(2.dp, Color.White, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected Color",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verified Creator Badge Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ZingraSurface, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Badge",
                            tint = ZingraCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Verified Creator Badge",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Displays official blue checkmark",
                                fontSize = 11.sp,
                                color = ZingraTextMuted
                            )
                        }
                    }

                    Switch(
                        checked = isVerified,
                        onCheckedChange = { isVerified = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = ZingraCyan,
                            uncheckedThumbColor = ZingraTextMuted,
                            uncheckedTrackColor = ZingraSurfaceVariant
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        displayName.ifBlank { "You (Creator)" },
                        formattedHandle,
                        bio.ifBlank { "Creating ad-free social video on Zingra ⚡" },
                        selectedColor,
                        isVerified
                    )
                },
                modifier = Modifier.testTag("save_profile_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ZingraCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", color = Color.White)
            }
        },
        containerColor = ZingraSurface
    )
}
