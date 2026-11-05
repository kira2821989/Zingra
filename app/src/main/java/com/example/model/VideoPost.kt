package com.example.model

data class VideoPost(
    val id: String,
    val creatorName: String,
    val creatorHandle: String,
    val avatarLetter: String,
    val avatarColorHex: Long = 0xFF6366F1,
    val timeAgo: String,
    val caption: String,
    val hashtags: List<String> = emptyList(),
    val videoUrl: String,
    val thumbnailUrl: String = "",
    val durationText: String = "0:30",
    val likesCount: Int = 1200,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val isFollowing: Boolean = false,
    val commentsCount: Int = 24,
    val sharesCount: Int = 45,
    val category: String = "Trending",
    val audioTrackName: String = "Original Sound - Zingra Beats",
    val viewsText: String = "1.2M views",
    val isAudioOnly: Boolean = false,
    val bestMomentTimestamp: String = "00:45",
    val bestMomentTitle: String = "Skip to Climax / Best Part"
)

data class Comment(
    val id: String,
    val postId: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val authorColorHex: Long = 0xFF38BDF8,
    val text: String,
    val timeAgo: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val timestampTag: String? = null
)

data class ChatMessage(
    val id: String,
    val roomId: String,
    val sender: String,
    val text: String,
    val cipherSnippet: String,
    val timestamp: String,
    val isMe: Boolean,
    val isRead: Boolean = true,
    val attachedVideoId: String? = null,
    val attachedVideoTitle: String? = null,
    val attachedTimestamp: String? = null
)

data class WatchPartyRoom(
    val roomId: String,
    val videoTitle: String,
    val videoUrl: String,
    val creatorName: String,
    val isPlaying: Boolean = true,
    val positionMs: Long = 0L,
    val participantCount: Int = 2,
    val participants: List<String> = listOf("Aman (Host)", "Rahul", "Priya")
)

enum class ZingraTab {
    FEED,
    WATCH_PARTY,
    CHAT,
    PROFILE
}
