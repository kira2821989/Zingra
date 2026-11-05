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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ZingraViewModel
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.ZingraBackground
import com.example.ui.theme.ZingraBorder
import com.example.ui.theme.ZingraCyan
import com.example.ui.theme.ZingraGreen
import com.example.ui.theme.ZingraIndigo
import com.example.ui.theme.ZingraPink
import com.example.ui.theme.ZingraSurface
import com.example.ui.theme.ZingraSurfaceVariant
import com.example.ui.theme.ZingraTextMuted
import com.example.ui.theme.ZingraTextSecondary

data class PartyReaction(val id: Long, val sender: String, val text: String, val colorHex: Long)

@Composable
fun WatchPartyScreen(
    viewModel: ZingraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val watchParty by viewModel.activeWatchParty.collectAsState()
    var reactionInput by remember { mutableStateOf("") }

    val liveReactions = remember {
        mutableStateListOf(
            PartyReaction(1, "Aman", "Sync quality is awesome! 🔥", 0xFFE11D48),
            PartyReaction(2, "Priya", "Sound sync is on point! 🍿", 0xFFA855F7),
            PartyReaction(3, "You", "No buffering, pure 60FPS! ⚡", 0xFF38BDF8)
        )
    }

    val quickEmojis = listOf("🔥", "🍿", "❤️", "🚀", "😂", "👏")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ZingraBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZingraSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Watch Party",
                        tint = ZingraCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Watch Party",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Live Sync Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(ZingraGreen.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(ZingraGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SYNCED LIVE",
                        color = ZingraGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Synchronized Video Player
            VideoPlayerView(
                videoUrl = watchParty.videoUrl,
                thumbnailUrl = "",
                durationText = "Live Sync",
                autoPlay = true,
                isFeedActive = true
            )

            // Room Info & Invite Friends Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = ZingraSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = watchParty.videoTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Hosted by ${watchParty.creatorName}",
                        fontSize = 12.sp,
                        color = ZingraTextMuted,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Room Code Chip
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(ZingraSurfaceVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Private Room",
                                tint = ZingraCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Room: ${watchParty.roomId}",
                                color = ZingraCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Invite Button with Android Share Sheet
                        Button(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "⚡ Join my Zingra Watch Party room: ${watchParty.roomId} to watch '${watchParty.videoTitle}' together in sync!"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Invite to Watch Party"))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ZingraIndigo,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("invite_watch_party_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Invite Friend", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Participants Row
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Active Viewers (${watchParty.participants.size})",
                        fontSize = 11.sp,
                        color = ZingraTextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                    LazyRow(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(watchParty.participants) { participant ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(ZingraSurfaceVariant, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(ZingraCyan),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = participant.first().toString(),
                                        fontSize = 10.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = participant,
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Live Reaction & Watch Chat Stream
            Text(
                text = "Live Stream Reactions",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ZingraTextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(liveReactions, key = { it.id }) { reaction ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ZingraSurface, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = reaction.sender,
                            color = Color(reaction.colorHex),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = reaction.text,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Quick Emojis
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZingraSurface)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                quickEmojis.forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .clickable {
                                liveReactions.add(
                                    PartyReaction(System.currentTimeMillis(), "You", emoji, 0xFF38BDF8)
                                )
                            }
                            .padding(4.dp)
                    )
                }
            }

            // Chat Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZingraSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = reactionInput,
                    onValueChange = { reactionInput = it },
                    placeholder = { Text("Send live reaction...", fontSize = 12.sp, color = ZingraTextMuted) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ZingraSurfaceVariant,
                        unfocusedContainerColor = ZingraSurfaceVariant,
                        focusedBorderColor = ZingraCyan,
                        unfocusedBorderColor = ZingraBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (reactionInput.isNotBlank()) {
                            liveReactions.add(
                                PartyReaction(System.currentTimeMillis(), "You", reactionInput.trim(), 0xFF38BDF8)
                            )
                            reactionInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ZingraCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
