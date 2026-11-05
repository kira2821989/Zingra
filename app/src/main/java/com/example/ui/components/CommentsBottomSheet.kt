package com.example.ui.components

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.Comment
import com.example.model.VideoPost
import com.example.ui.theme.ZingraBackground
import com.example.ui.theme.ZingraBorder
import com.example.ui.theme.ZingraCyan
import com.example.ui.theme.ZingraPink
import com.example.ui.theme.ZingraSurface
import com.example.ui.theme.ZingraSurfaceVariant
import com.example.ui.theme.ZingraTextMuted
import com.example.ui.theme.ZingraTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    post: VideoPost,
    comments: List<Comment>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit,
    onToggleCommentLike: (String) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }
    val quickEmojis = listOf("🔥", "❤️", "🚀", "👏", "😍", "⚡", "🙌")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ZingraSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 38.dp, height = 4.dp)
                    .background(ZingraBorder, RoundedCornerShape(2.dp))
            )
        },
        modifier = Modifier.fillMaxHeight(0.85f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Comments (${comments.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "on ${post.creatorHandle}'s video",
                        fontSize = 12.sp,
                        color = ZingraTextMuted
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Comments",
                        tint = ZingraTextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ZingraBorder)
            )

            // Comments List
            if (comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No comments yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ZingraTextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Be the first to share your thoughts!",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZingraTextMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(comments, key = { it.id }) { comment ->
                        CommentItem(
                            comment = comment,
                            onLikeClick = { onToggleCommentLike(comment.id) }
                        )
                    }
                }
            }

            // Quick Emoji Reaction Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZingraBackground)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                quickEmojis.forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .clickable {
                                newCommentText += emoji
                            }
                            .padding(4.dp)
                    )
                }
            }

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZingraSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Your avatar
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(ZingraCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Y",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = {
                        Text(
                            text = "Add a comment...",
                            fontSize = 13.sp,
                            color = ZingraTextMuted
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("comment_input_field"),
                    shape = RoundedCornerShape(24.dp),
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
                        if (newCommentText.isNotBlank()) {
                            onAddComment(newCommentText)
                            newCommentText = ""
                        }
                    },
                    enabled = newCommentText.isNotBlank(),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (newCommentText.isNotBlank()) ZingraCyan else ZingraSurfaceVariant)
                        .testTag("send_comment_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Post Comment",
                        tint = if (newCommentText.isNotBlank()) Color.Black else ZingraTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Author Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(comment.authorColorHex)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.authorAvatar,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.authorName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = comment.timeAgo,
                    fontSize = 11.sp,
                    color = ZingraTextMuted
                )
            }

            Text(
                text = comment.text,
                fontSize = 13.sp,
                color = Color(0xFFE2E8F0),
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Like comment button & count
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onLikeClick() }
                .padding(start = 6.dp)
        ) {
            Icon(
                imageVector = if (comment.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Like Comment",
                tint = if (comment.isLiked) ZingraPink else ZingraTextMuted,
                modifier = Modifier.size(16.dp)
            )
            if (comment.likesCount > 0) {
                Text(
                    text = comment.likesCount.toString(),
                    fontSize = 11.sp,
                    color = if (comment.isLiked) ZingraPink else ZingraTextMuted
                )
            }
        }
    }
}
