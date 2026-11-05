package com.example.model

data class UserProfile(
    val id: String = "user_me",
    val displayName: String = "You (Creator)",
    val username: String = "@you_zingra",
    val bio: String = "Creating open-source, ad-free social video and private watch party streams ⚡",
    val avatarColorHex: Long = 0xFF38BDF8,
    val avatarLetter: String = "Y",
    val isVerifiedCreator: Boolean = true,
    val followersCount: Int = 1420,
    val followingCount: Int = 28,
    val totalLikes: String = "42.8K",
    val videosCount: Int = 5
)
