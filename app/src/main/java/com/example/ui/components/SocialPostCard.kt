package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoPost
import com.example.ui.theme.ZingraBorder
import com.example.ui.theme.ZingraCyan
import com.example.ui.theme.ZingraGreen
import com.example.ui.theme.ZingraIndigo
import com.example.ui.theme.ZingraPink
import com.example.ui.theme.ZingraSurface
import com.example.ui.theme.ZingraSurfaceVariant
import com.example.ui.theme.ZingraTextMuted
import com.example.ui.theme.ZingraTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SocialPostCard(
    post: VideoPost,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onFollowClick: () -> Unit,
    onWatchPartyClick: () -> Unit,
    onToggleAudioOnly: () -> Unit,
    onJumpToBestMoment: () -> Unit,
    onShareToSecretChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    // Heart bounce animation
    val heartScale by animateFloatAsState(
        targetValue = if (post.isLiked) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heartScale"
    )

    val heartColor by animateColorAsState(
        targetValue = if (post.isLiked) ZingraPink else ZingraTextSecondary,
        label = "heartColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("post_card_${post.id}"),
        colors = CardDefaults.cardColors(containerColor = ZingraSurface),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // 1. Post Header: Creator Info + Follow button + More Menu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Avatar circle with creator initial
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(post.avatarColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.avatarLetter,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = post.creatorName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.creatorHandle,
                                fontSize = 12.sp,
                                color = ZingraTextMuted
                            )
                            Text(
                                text = " • ${post.timeAgo}",
                                fontSize = 12.sp,
                                color = ZingraTextMuted
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Follow button
                    Button(
                        onClick = onFollowClick,
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("follow_button_${post.id}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (post.isFollowing) Color(0xFF272F3D) else ZingraIndigo,
                            contentColor = if (post.isFollowing) ZingraTextSecondary else Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (post.isFollowing) "Following" else "Follow",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = ZingraTextMuted
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🔒 Send to Secret Chat") },
                                onClick = {
                                    showMenu = false
                                    onShareToSecretChat()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (post.isAudioOnly) "🎬 Switch to Video Mode" else "📻 Audio-Only (Save 90% Data)") },
                                onClick = {
                                    showMenu = false
                                    onToggleAudioOnly()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("👥 Start Watch Party") },
                                onClick = {
                                    showMenu = false
                                    onWatchPartyClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("↗ Share External Link") },
                                onClick = {
                                    showMenu = false
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Check out this video on Zingra: ${post.caption}")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Zingra Video"))
                                }
                            )
                        }
                    }
                }
            }

            // 2. Video Player area (Supports Audio-Only Podcast mode)
            VideoPlayerView(
                videoUrl = post.videoUrl,
                thumbnailUrl = post.thumbnailUrl,
                durationText = post.durationText,
                isAudioOnly = post.isAudioOnly,
                audioTrackName = post.audioTrackName,
                onDoubleTapLike = onLikeClick
            )

            // KILLER FEATURE BAR: 1-Tap "Skip to Best Part" Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .clickable { onJumpToBestMoment() }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Best Part",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = post.bestMomentTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF8FAFC)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFFF59E0B).copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Jump to ${post.bestMomentTimestamp} ➔",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBBF24)
                    )
                }
            }

            // 3. Social Actions Row: Like, Comment, Audio Mode, Watch Party, Share, Bookmark
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Like button & counter
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onLikeClick() }
                            .testTag("like_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (post.isLiked) "Unlike" else "Like",
                            tint = heartColor,
                            modifier = Modifier
                                .size(24.dp)
                                .scale(heartScale)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formatCount(post.likesCount),
                            color = heartColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Comment button & counter
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onCommentClick() }
                            .testTag("comment_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = ZingraTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formatCount(post.commentsCount),
                            color = ZingraTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // KILLER UNIQUE FEATURE: 1-Tap Audio Only / Podcast Mode Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onToggleAudioOnly() }
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (post.isAudioOnly) ZingraGreen.copy(alpha = 0.2f) else Color.Transparent)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("audio_only_toggle_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isAudioOnly) Icons.Default.Videocam else Icons.Default.Headphones,
                            contentDescription = "Toggle Audio Mode",
                            tint = if (post.isAudioOnly) ZingraGreen else ZingraTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (post.isAudioOnly) "Video" else "Podcast",
                            color = if (post.isAudioOnly) ZingraGreen else ZingraTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Share to Secret Chat Icon
                    IconButton(
                        onClick = onShareToSecretChat,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Send to Secret Chat",
                            tint = ZingraCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Watch Party Co-watch Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(ZingraCyan, ZingraIndigo)
                                )
                            )
                            .clickable { onWatchPartyClick() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("watch_party_btn_${post.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Watch Party",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Watch Party",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Bookmark Button
                    IconButton(
                        onClick = onBookmarkClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (post.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (post.isBookmarked) ZingraCyan else ZingraTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // 4. Caption & Hashtags
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                val captionText = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                        append("${post.creatorHandle}  ")
                    }
                    append(if (isExpanded || post.caption.length <= 80) post.caption else post.caption.take(80) + "...")
                }

                Text(
                    text = captionText,
                    fontSize = 13.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 18.sp,
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                )

                if (post.caption.length > 80) {
                    Text(
                        text = if (isExpanded) "less" else "more",
                        fontSize = 12.sp,
                        color = ZingraTextMuted,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { isExpanded = !isExpanded }
                    )
                }

                // Colored hashtags
                if (post.hashtags.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        post.hashtags.forEach { tag ->
                            Text(
                                text = tag,
                                color = ZingraCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Audio Track Marquee
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Sound Track",
                        tint = ZingraTextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.audioTrackName,
                        fontSize = 11.sp,
                        color = ZingraTextMuted
                    )
                }

                // View comments prompt
                if (post.commentsCount > 0) {
                    Text(
                        text = "View all ${post.commentsCount} comments",
                        color = ZingraTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(top = 6.dp, bottom = 4.dp)
                            .clickable { onCommentClick() }
                    )
                }
            }

            // Bottom Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color(0xFF070A11))
            )
        }
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000f)
        count >= 1_000 -> String.format("%.1fK", count / 1_000f)
        else -> count.toString()
    }
}
