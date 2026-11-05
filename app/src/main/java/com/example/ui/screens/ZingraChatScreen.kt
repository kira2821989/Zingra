package com.example.ui.screens

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.ui.ZingraViewModel
import com.example.ui.theme.WhatsAppBubbleReceived
import com.example.ui.theme.WhatsAppBubbleSent
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppYellow
import com.example.ui.theme.ZingraCyan

@Composable
fun ZingraChatScreen(
    viewModel: ZingraViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val posts by viewModel.posts.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B141A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {

            // 1. WhatsApp Dark Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF202C33))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE11D48)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Aman Verma",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE9EDEF)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(WhatsAppGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "online • End-to-End Encrypted",
                                fontSize = 11.sp,
                                color = Color(0xFF8696A0)
                            )
                        }
                    }
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color(0xFFAEBAC1)
                    )
                }
            }

            // 2. Yellow Security Shield E2EE Notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .background(Color(0xFF182229), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security",
                        tint = WhatsAppYellow,
                        modifier = Modifier
                            .size(14.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Messages and calls are end-to-end encrypted. No one outside of this chat, not even Zingra, can read or listen to them.",
                        color = WhatsAppYellow,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // 3. Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(
                        message = msg,
                        onJumpToVideo = { videoId ->
                            val post = posts.find { it.id == videoId } ?: posts.firstOrNull()
                            if (post != null) {
                                viewModel.startWatchParty(post)
                            }
                        }
                    )
                }
            }

            // 4. WhatsApp Style Bottom Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF202C33))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Input Container
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF2A3942), RoundedCornerShape(24.dp))
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentSatisfiedAlt,
                            contentDescription = "Emoji",
                            tint = Color(0xFF8696A0),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Message (Encrypted)",
                                color = Color(0xFF8696A0),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color(0xFFE9EDEF),
                            unfocusedTextColor = Color(0xFFE9EDEF)
                        ),
                        singleLine = false,
                        maxLines = 4
                    )

                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach",
                            tint = Color(0xFF8696A0),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Green Send Button (or Mic icon when empty)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(WhatsAppGreen)
                        .testTag("send_chat_button"),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendChatMessage(inputText)
                                inputText = ""
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (inputText.isNotBlank()) Icons.Default.Send else Icons.Default.Mic,
                            contentDescription = if (inputText.isNotBlank()) "Send" else "Voice",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onJumpToVideo: (String) -> Unit
) {
    val isMe = message.isMe
    val align = if (isMe) Alignment.End else Alignment.Start
    val bgColor = if (isMe) WhatsAppBubbleSent else WhatsAppBubbleReceived

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = bgColor,
                    shape = RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp,
                        bottomStart = if (isMe) 10.dp else 2.dp,
                        bottomEnd = if (isMe) 2.dp else 10.dp
                    )
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    color = Color(0xFFE9EDEF),
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )

                // UNIQUE FEATURE: Attached Video Card with 1-click jump to Watch Party Climax
                if (message.attachedVideoId != null) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .clickable { onJumpToVideo(message.attachedVideoId) }
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "Play Climax",
                                tint = ZingraCyan,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = message.attachedVideoTitle ?: "Shared Video Clip",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "⚡ Tap to Jump to Climax (${message.attachedTimestamp ?: "00:00"})",
                                    color = ZingraCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Cipher snippet demonstrating zero knowledge privacy
                Text(
                    text = "🔒 ${message.cipherSnippet}",
                    color = if (isMe) Color(0xFF8FD4C6) else Color(0xFF8696A0),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 2.dp)
                )

                // Time + double checkmark
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp,
                        color = Color(0xFF8696A0),
                        fontSize = 10.sp
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = Color(0xFF53BDEB),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
