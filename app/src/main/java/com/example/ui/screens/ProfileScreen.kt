package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ZingraViewModel
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.UserSearchDialog
import com.example.ui.theme.ZingraBackground
import com.example.ui.theme.ZingraBorder
import com.example.ui.theme.ZingraCyan
import com.example.ui.theme.ZingraGreen
import com.example.ui.theme.ZingraIndigo
import com.example.ui.theme.ZingraSurface
import com.example.ui.theme.ZingraSurfaceVariant
import com.example.ui.theme.ZingraTextMuted
import com.example.ui.theme.ZingraTextSecondary

@Composable
fun ProfileScreen(
    viewModel: ZingraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by viewModel.currentUserProfile.collectAsState()
    val showEditProfile by viewModel.showEditProfileDialog.collectAsState()
    val showUserSearch by viewModel.showUserSearchDialog.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    var showLicenseDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ZingraBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZingraSurface)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "My Zingra Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Search Users button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ZingraSurfaceVariant)
                        .clickable { viewModel.openUserSearch() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("profile_search_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔍 Find Friends",
                        color = ZingraCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // User Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Dynamic Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(profile.avatarColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.avatarLetter,
                        color = Color.Black,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (profile.isVerifiedCreator) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Creator",
                            tint = ZingraCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = profile.username,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZingraCyan,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = profile.bio,
                    fontSize = 13.sp,
                    color = ZingraTextSecondary,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp)
                )

                // Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .background(ZingraSurface, RoundedCornerShape(16.dp))
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(number = "${profile.videosCount}", label = "Videos")
                    StatItem(number = "${profile.followersCount}", label = "Followers")
                    StatItem(number = "${profile.followingCount}", label = "Following")
                    StatItem(number = profile.totalLikes, label = "Likes")
                }

                // Action Buttons: Edit Profile + Share Invite Link + Create Post
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.openEditProfile() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("edit_profile_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZingraCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Edit Handle", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val inviteLink = viewModel.generateInviteLink()
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "⚡ Connect with me on Zingra (@${profile.username.removePrefix("@")})! Ad-free social video and private watch parties: $inviteLink"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Zingra Profile"))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_profile_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZingraSurfaceVariant,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Share Link", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.openCreatePost() },
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("new_video_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZingraIndigo,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("+ Video", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Open Source Community Section
            Text(
                text = "Open Source Community",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = ZingraSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ZingraGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = "Open Source",
                                    tint = ZingraGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "100% Open Source",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "GNU AGPLv3 Licensed",
                                    fontSize = 12.sp,
                                    color = ZingraGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(ZingraGreen.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "VERIFIED",
                                color = ZingraGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Zingra code is 100% public and auditable on GitHub. No closed algorithms, no hidden surveillance, zero tracking, and no corporate takeover.",
                        fontSize = 12.sp,
                        color = ZingraTextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showLicenseDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("view_license_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "License",
                                modifier = Modifier.size(16.dp),
                                tint = ZingraCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("License", color = Color.White, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "⚡ Zingra is 100% open-source! Check out our GitHub repository: https://github.com/zingra-app/zingra-android")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Zingra GitHub Repo"))
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("github_repo_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ZingraIndigo,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Repo",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Repo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Zingra Privacy & Superpowers Cards
            Text(
                text = "Zingra Superpowers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureCard(
                    icon = Icons.Default.VisibilityOff,
                    iconColor = ZingraGreen,
                    title = "100% Ad-Free Experience",
                    description = "No 15-second unskippable video ads or sponsored interruptions. Smooth immediate streaming."
                )
                FeatureCard(
                    icon = Icons.Default.Lock,
                    iconColor = ZingraCyan,
                    title = "Zero-Knowledge Encryption",
                    description = "Private keys stay strictly inside your device. Chats are end-to-end encrypted with AES-256."
                )
                FeatureCard(
                    icon = Icons.Default.Speed,
                    iconColor = Color(0xFFF59E0B),
                    title = "Synchronized Watch Parties",
                    description = "Co-watch high quality movies and streams with friends anywhere with synchronized playback."
                )
                FeatureCard(
                    icon = Icons.Default.Shield,
                    iconColor = ZingraIndigo,
                    title = "Zero Tracking & Zero Phone Numbers",
                    description = "Enjoy content without invasive tracking, cookies or mandatory personal phone numbers."
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Zingra v1.0.0 • GNU AGPLv3 Open Source Platform",
                fontSize = 11.sp,
                color = ZingraTextMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Open Source License Dialog
        if (showLicenseDialog) {
            AlertDialog(
                onDismissRequest = { showLicenseDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "AGPL License",
                            tint = ZingraGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GNU AGPLv3 License", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Freedom & Transparency Guarantee:",
                            fontWeight = FontWeight.Bold,
                            color = ZingraCyan,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "1. Freedom to Run: Anyone is free to run Zingra on any device without paywalls or restrictive licenses.\n\n" +
                                    "2. Freedom to Study: The source code is completely open. Anyone can inspect how encryption and video playback work.\n\n" +
                                    "3. Freedom to Modify: You can fork and improve Zingra for the community.\n\n" +
                                    "4. Copyleft Protection: Under AGPLv3, nobody can steal Zingra's code and lock it behind closed-source proprietary software.\n\n" +
                                    "5. Zero Data Harvest: We never collect or sell your private phone numbers, browsing history or personal messages.",
                            fontSize = 12.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 17.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showLicenseDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = ZingraCyan, contentColor = Color.Black)
                    ) {
                        Text("Got It!", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = ZingraSurface
            )
        }

        // Edit Profile & Handle Customization Dialog
        if (showEditProfile) {
            EditProfileDialog(
                currentProfile = profile,
                onDismiss = { viewModel.closeEditProfile() },
                onSave = { displayName, username, bio, avatarColorHex, isVerified ->
                    viewModel.updateProfile(displayName, username, bio, avatarColorHex, isVerified)
                }
            )
        }

        // Search Creators & Friends Dialog
        if (showUserSearch) {
            UserSearchDialog(
                searchQuery = searchQuery,
                searchResults = searchResults,
                currentUserProfile = profile,
                inviteLink = viewModel.generateInviteLink(),
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onDismiss = { viewModel.closeUserSearch() },
                onStartChat = { user -> viewModel.startChatWithUser(user) },
                onShowToast = { msg -> viewModel.showToast(msg) }
            )
        }
    }
}

@Composable
fun StatItem(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = ZingraTextMuted
        )
    }
}

@Composable
fun FeatureCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ZingraSurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = ZingraTextSecondary,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
