package com.example.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.ZingraCyan
import com.example.ui.theme.ZingraGreen
import com.example.ui.theme.ZingraPink
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerView(
    videoUrl: String,
    thumbnailUrl: String,
    durationText: String,
    isAudioOnly: Boolean = false,
    audioTrackName: String = "Original Sound",
    autoPlay: Boolean = false,
    isFeedActive: Boolean = true,
    onDoubleTapLike: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlayerActive by remember { mutableStateOf(autoPlay) }
    var isPlaying by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var showBigHeart by remember { mutableStateOf(false) }
    var isVideoReady by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    val heartScale = remember { Animatable(0f) }

    // Vinyl rotation animation for audio-only mode
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    LaunchedEffect(showBigHeart) {
        if (showBigHeart) {
            heartScale.snapTo(0f)
            heartScale.animateTo(
                targetValue = 1.2f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            delay(500)
            heartScale.animateTo(0f)
            showBigHeart = false
        }
    }

    DisposableEffect(videoUrl) {
        onDispose {
            try {
                videoViewRef?.stopPlayback()
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)
            .pointerInput(isPlayerActive) {
                detectTapGestures(
                    onDoubleTap = {
                        showBigHeart = true
                        onDoubleTapLike()
                    },
                    onTap = {
                        if (!isPlayerActive) {
                            isPlayerActive = true
                            isLoading = true
                        } else {
                            val vv = videoViewRef
                            if (vv != null) {
                                if (vv.isPlaying) {
                                    vv.pause()
                                    isPlaying = false
                                } else {
                                    vv.start()
                                    isPlaying = true
                                }
                            }
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // AUDIO-ONLY PODCAST VISUALIZER (When audio-only mode is active)
        if (isAudioOnly) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF1E1B4B), Color(0xFF090D16))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Rotating Vinyl Record Disc
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .rotate(if (isPlayerActive) rotationAngle else 0f)
                            .clip(CircleShape)
                            .background(Color(0xFF111827)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1F2937)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(ZingraCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = "Vinyl Note",
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "Podcast Mode",
                            tint = ZingraGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Audio Podcast Mode Active",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "⚡ Saves 90% Mobile Data • Screen-Off Friendly",
                        fontSize = 10.sp,
                        color = ZingraGreen,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        } else if (isPlayerActive) {
            // Lazy Android native VideoView (Only mounts when user taps to play)
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    VideoView(ctx).apply {
                        // Crucial: Set error listener BEFORE setVideoURI to handle network/decoder issues gracefully
                        setOnErrorListener { _, what, extra ->
                            // Consume error to prevent MediaPlayer crash/dialog
                            isVideoReady = false
                            isPlaying = false
                            isLoading = false
                            true
                        }
                        setOnPreparedListener { mp ->
                            isVideoReady = true
                            isLoading = false
                            mp.isLooping = true
                            mp.setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
                            start()
                            isPlaying = true
                        }
                        try {
                            setVideoURI(Uri.parse(videoUrl))
                        } catch (e: Exception) {
                            isVideoReady = false
                            isLoading = false
                        }
                        videoViewRef = this
                    }
                },
                update = { view ->
                    videoViewRef = view
                }
            )
        }

        // Show Thumbnail if not playing or before player is activated
        if (!isPlayerActive || (!isVideoReady && thumbnailUrl.isNotBlank())) {
            if (thumbnailUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Video Thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Big Centered Play Circle Icon
            if (!isLoading) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        // Loading spinner while connecting to stream
        if (isLoading) {
            CircularProgressIndicator(
                color = ZingraCyan,
                modifier = Modifier.size(38.dp),
                strokeWidth = 3.dp
            )
        }

        // Overlay: Pause Indicator icon when paused during active playback
        AnimatedVisibility(
            visible = isPlayerActive && !isPlaying && isVideoReady && !isAudioOnly && !isLoading,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Video",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Animated Double-Tap Big Heart
        if (showBigHeart) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Liked Heart",
                tint = ZingraPink,
                modifier = Modifier
                    .size(90.dp * heartScale.value)
            )
        }

        // Bottom right duration badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isAudioOnly) "AUDIO ONLY" else durationText,
                color = if (isAudioOnly) ZingraGreen else Color.White,
                fontSize = 11.sp,
                fontWeight = if (isAudioOnly) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.labelSmall
            )
        }

        // Bottom left mute/unmute toggle (Visible when player is active)
        if (isPlayerActive && !isAudioOnly) {
            IconButton(
                onClick = {
                    isMuted = !isMuted
                    videoViewRef?.let { vv ->
                        // toggle
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .testTag("mute_toggle_button")
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                    contentDescription = if (isMuted) "Unmute" else "Mute",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
