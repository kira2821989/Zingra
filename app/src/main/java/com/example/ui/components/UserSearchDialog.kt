package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.UserProfile
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
fun UserSearchDialog(
    searchQuery: String,
    searchResults: List<UserProfile>,
    currentUserProfile: UserProfile,
    inviteLink: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onStartChat: (UserProfile) -> Unit,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    val followedMap = remember { mutableStateMapOf<String, Boolean>() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(20.dp),
            color = ZingraSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ZingraCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Users",
                                tint = ZingraCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Find Creators & Friends",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Search by @handle or display name",
                                fontSize = 11.sp,
                                color = ZingraTextMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_search_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Close",
                            tint = ZingraTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    placeholder = {
                        Text("Search @username or name...", color = ZingraTextMuted, fontSize = 13.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ZingraCyan
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = ZingraTextMuted
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_query"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ZingraCyan,
                        unfocusedBorderColor = ZingraBorder,
                        focusedContainerColor = ZingraBackground,
                        unfocusedContainerColor = ZingraBackground
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 1-Click Invite Link Share Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ZingraSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Your 1-Click Invite Link:",
                                fontSize = 11.sp,
                                color = ZingraTextMuted
                            )
                            Text(
                                text = inviteLink,
                                fontSize = 12.sp,
                                color = ZingraCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Zingra Profile", inviteLink)
                                    clipboard.setPrimaryClip(clip)
                                    onShowToast("📋 Copied invite link: $inviteLink")
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Link",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "⚡ Connect with me on Zingra! Join ad-free social video and private watch parties: $inviteLink"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Zingra Profile Link"))
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Link",
                                    tint = ZingraGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Results Counter & Header
                Text(
                    text = if (searchQuery.isBlank()) "Recommended Creators" else "Search Results (${searchResults.size})",
                    fontSize = 12.sp,
                    color = ZingraTextMuted,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Results List
                if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No users found for '$searchQuery'",
                                color = ZingraTextSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Invite them using your profile link above!",
                                color = ZingraCyan,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults, key = { it.id }) { user ->
                            val isFollowed = followedMap[user.id] == true
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = ZingraSurfaceVariant),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // User Avatar Circle
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(user.avatarColorHex)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.avatarLetter,
                                            color = Color.Black,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // Name, Handle, Bio & Stats
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = user.displayName,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            if (user.isVerifiedCreator) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Verified Creator",
                                                    tint = ZingraCyan,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = user.username,
                                            color = ZingraCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Text(
                                            text = user.bio,
                                            color = ZingraTextSecondary,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            lineHeight = 14.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )

                                        Text(
                                            text = "${user.followersCount} followers • ${user.videosCount} videos",
                                            color = ZingraTextMuted,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Action Buttons: Follow + Chat
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Chat Button
                                        IconButton(
                                            onClick = { onStartChat(user) },
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(ZingraIndigo.copy(alpha = 0.2f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Chat,
                                                contentDescription = "Start Chat",
                                                tint = ZingraCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        // Follow Button
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    if (isFollowed) Color(0xFF334155) else ZingraCyan
                                                )
                                                .clickable {
                                                    val newState = !isFollowed
                                                    followedMap[user.id] = newState
                                                    val msg = if (newState) "Followed ${user.username}!" else "Unfollowed ${user.username}"
                                                    onShowToast(msg)
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isFollowed) "Following" else "Follow",
                                                color = if (isFollowed) Color.White else Color.Black,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
