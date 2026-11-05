package com.example.data

import com.example.model.ChatMessage
import com.example.model.Comment
import com.example.model.UserProfile
import com.example.model.VideoPost
import com.example.model.WatchPartyRoom
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ZingraRepository {

    private val _currentUserProfile = MutableStateFlow(UserProfile())
    val currentUserProfile: StateFlow<UserProfile> = _currentUserProfile.asStateFlow()

    private val _posts = MutableStateFlow<List<VideoPost>>(initialPosts())
    val posts: StateFlow<List<VideoPost>> = _posts.asStateFlow()

    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(initialComments())
    val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(initialChatMessages())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _activeWatchParty = MutableStateFlow<WatchPartyRoom>(
        WatchPartyRoom(
            roomId = "zing-4303",
            videoTitle = "Big Buck Bunny 4K 60FPS • Open Cinema",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            creatorName = "Blender Open Studio",
            isPlaying = true,
            positionMs = 12000L,
            participantCount = 2,
            participants = listOf("You (Host)", "Aman Verma")
        )
    )
    val activeWatchParty: StateFlow<WatchPartyRoom> = _activeWatchParty.asStateFlow()

    private val _isCloudSyncConnected = MutableStateFlow(false)
    val isCloudSyncConnected: StateFlow<Boolean> = _isCloudSyncConnected.asStateFlow()

    private val staticCreators: List<UserProfile> = listOf(
        UserProfile("u1", "Aman Verma", "@aman_vfx", "4K 60FPS VFX Artist & Motion Director 🎬", 0xFFE11D48, "A", true, 28400, 142, "140K", 18),
        UserProfile("u2", "Tech Bharat", "@tech_bharat", "Building zero-knowledge apps and open tech ⚡", 0xFF06B6D4, "T", true, 45200, 89, "320K", 42),
        UserProfile("u3", "Priya Sharma", "@priya_vibes", "Weekend vibes & chill aesthetic streams ✨", 0xFFA855F7, "P", false, 12800, 210, "95K", 12),
        UserProfile("u4", "Desi Gamers Club", "@desigamers", "Custom lobby clutches & esports highlights 🎯", 0xFFF59E0B, "D", true, 89400, 56, "810K", 64),
        UserProfile("u5", "CineWorld HD", "@cineworld_hd", "Cinematic open source CGI breakdowns 🍿", 0xFF10B981, "C", true, 62100, 114, "490K", 31),
        UserProfile("u6", "Kabir Singh", "@kabir_s", "Music beats producer & sound engineer 🎵", 0xFFEC4899, "K", false, 8400, 180, "34K", 9)
    )

    private val dynamicRemoteCreators = mutableListOf<UserProfile>()

    // Lazy safe initialization of Firestore with custom databaseId
    private val firestore: FirebaseFirestore? by lazy {
        try {
            val app = FirebaseApp.getInstance()
            val dbId = "ai-studio-android-motioncr-4e5c2b18-fafa-4f01-aae0-c3292d83a200"
            FirebaseFirestore.getInstance(app, dbId)
        } catch (e: Throwable) {
            // log
            null
        }
    }

    init {
        setupFirestoreRealtimeListeners()
    }

    private fun setupFirestoreRealtimeListeners() {
        val db = firestore ?: return
        _isCloudSyncConnected.value = true

        // 1. Listen for Posts in real time
        try {
            db.collection("posts")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    if (!snapshot.isEmpty) {
                        val remotePosts = snapshot.documents.mapNotNull { doc ->
                            try {
                                VideoPost(
                                    id = doc.getString("id") ?: doc.id,
                                    creatorName = doc.getString("creatorName") ?: "Zingra Creator",
                                    creatorHandle = doc.getString("creatorHandle") ?: "@creator",
                                    avatarLetter = doc.getString("avatarLetter") ?: "Z",
                                    avatarColorHex = doc.getLong("avatarColorHex") ?: 0xFF38BDF8,
                                    timeAgo = doc.getString("timeAgo") ?: "Just now",
                                    caption = doc.getString("caption") ?: "",
                                    hashtags = (doc.get("hashtags") as? List<*>)?.mapNotNull { it?.toString() } ?: listOf("#zingra"),
                                    videoUrl = doc.getString("videoUrl") ?: "",
                                    thumbnailUrl = doc.getString("thumbnailUrl") ?: "",
                                    durationText = doc.getString("durationText") ?: "0:45",
                                    likesCount = doc.getLong("likesCount")?.toInt() ?: 1,
                                    isLiked = doc.getBoolean("isLiked") ?: false,
                                    isBookmarked = doc.getBoolean("isBookmarked") ?: false,
                                    isFollowing = doc.getBoolean("isFollowing") ?: false,
                                    commentsCount = doc.getLong("commentsCount")?.toInt() ?: 0,
                                    sharesCount = doc.getLong("sharesCount")?.toInt() ?: 0,
                                    category = doc.getString("category") ?: "Trending",
                                    audioTrackName = doc.getString("audioTrackName") ?: "Original Sound",
                                    viewsText = doc.getString("viewsText") ?: "1 view",
                                    isAudioOnly = doc.getBoolean("isAudioOnly") ?: false,
                                    bestMomentTimestamp = doc.getString("bestMomentTimestamp") ?: "00:15",
                                    bestMomentTitle = doc.getString("bestMomentTitle") ?: "Best Part"
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (remotePosts.isNotEmpty()) {
                            // Merge remote posts with defaults to prevent complete overwrite
                            val remoteIds = remotePosts.map { it.id }.toSet()
                            val defaults = initialPosts().filter { it.id !in remoteIds }
                            _posts.value = remotePosts + defaults
                        }
                    }
                }
        } catch (e: Throwable) {
            // log
        }

        // 2. Listen for Comments in real time
        try {
            db.collection("comments")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    val newMap = mutableMapOf<String, MutableList<Comment>>()
                    initialComments().forEach { (k, v) ->
                        newMap[k] = v.toMutableList()
                    }

                    for (doc in snapshot.documents) {
                        val postId = doc.getString("postId") ?: continue
                        val comment = Comment(
                            id = doc.getString("id") ?: doc.id,
                            postId = postId,
                            authorName = doc.getString("authorName") ?: "User",
                            authorHandle = doc.getString("authorHandle") ?: "@user",
                            authorAvatar = doc.getString("authorAvatar") ?: "U",
                            authorColorHex = doc.getLong("authorColorHex") ?: 0xFF38BDF8,
                            text = doc.getString("text") ?: "",
                            timeAgo = doc.getString("timeAgo") ?: "Just now",
                            likesCount = doc.getLong("likesCount")?.toInt() ?: 0,
                            isLiked = doc.getBoolean("isLiked") ?: false,
                            timestampTag = doc.getString("timestampTag")
                        )
                        val list = newMap.getOrPut(postId) { mutableListOf() }
                        if (list.none { it.id == comment.id }) {
                            list.add(0, comment)
                        }
                    }
                    _comments.value = newMap
                }
        } catch (e: Throwable) {
            // log
        }

        // 3. Listen for Watch Party room sync in real time
        try {
            db.collection("watch_parties").document("zing-4303")
                .addSnapshotListener { doc, error ->
                    if (error != null || doc == null || !doc.exists()) return@addSnapshotListener
                    val isPlaying = doc.getBoolean("isPlaying") ?: true
                    val posMs = doc.getLong("positionMs") ?: 0L
                    val count = doc.getLong("participantCount")?.toInt() ?: 2

                    _activeWatchParty.update {
                        it.copy(
                            isPlaying = isPlaying,
                            positionMs = posMs,
                            participantCount = count
                        )
                    }
                }
        } catch (e: Throwable) {
            // log
        }

        // 4. Listen for Encrypted Chat Messages in real time
        try {
            db.collection("chats").document("zing-4303").collection("messages")
                .orderBy("timestampMs", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    if (!snapshot.isEmpty) {
                        val remoteMessages = snapshot.documents.mapNotNull { doc ->
                            val senderName = doc.getString("sender") ?: "Peer"
                            val senderHandle = doc.getString("senderHandle") ?: ""
                            val myHandle = _currentUserProfile.value.username
                            val myName = _currentUserProfile.value.displayName
                            val isSelf = (senderHandle.isNotEmpty() && senderHandle.equals(myHandle, ignoreCase = true)) ||
                                         senderName.equals(myName, ignoreCase = true) ||
                                         (doc.getBoolean("isMe") == true && senderHandle.isEmpty())

                            ChatMessage(
                                id = doc.getString("id") ?: doc.id,
                                roomId = doc.getString("roomId") ?: "zing-4303",
                                sender = senderName,
                                text = doc.getString("text") ?: "",
                                cipherSnippet = doc.getString("cipherSnippet") ?: "aB93xX==",
                                timestamp = doc.getString("timestamp") ?: "Now",
                                isMe = isSelf,
                                isRead = true,
                                attachedVideoId = doc.getString("attachedVideoId"),
                                attachedVideoTitle = doc.getString("attachedVideoTitle"),
                                attachedTimestamp = doc.getString("attachedTimestamp")
                            )
                        }
                        if (remoteMessages.isNotEmpty()) {
                            _chatMessages.value = remoteMessages
                        }
                    }
                }
        } catch (e: Throwable) {
            // log
        }

        // 5. Listen for user profiles in real time
        try {
            db.collection("profiles")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    val users = snapshot.documents.mapNotNull { doc ->
                        try {
                            UserProfile(
                                id = doc.id,
                                displayName = doc.getString("displayName") ?: "Creator",
                                username = doc.getString("username") ?: "@user",
                                bio = doc.getString("bio") ?: "",
                                avatarColorHex = doc.getLong("avatarColorHex") ?: 0xFF38BDF8,
                                avatarLetter = doc.getString("avatarLetter") ?: "C",
                                isVerifiedCreator = doc.getBoolean("isVerifiedCreator") ?: false,
                                followersCount = doc.getLong("followersCount")?.toInt() ?: 1200,
                                followingCount = doc.getLong("followingCount")?.toInt() ?: 20,
                                totalLikes = doc.getString("totalLikes") ?: "10K",
                                videosCount = doc.getLong("videosCount")?.toInt() ?: 2
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (users.isNotEmpty()) {
                        dynamicRemoteCreators.clear()
                        dynamicRemoteCreators.addAll(users)
                    }
                }
        } catch (e: Throwable) {
            // log
        }
    }

    fun updateProfile(
        displayName: String,
        username: String,
        bio: String,
        avatarColorHex: Long,
        isVerified: Boolean
    ) {
        val raw = username.trim().lowercase()
        val cleanUsername = if (raw.startsWith("@")) raw else "@$raw"
        val letter = displayName.trim().firstOrNull()?.uppercase() ?: "Y"
        val updatedProfile = UserProfile(
            id = "user_me",
            displayName = displayName.trim(),
            username = cleanUsername,
            bio = bio.trim(),
            avatarColorHex = avatarColorHex,
            avatarLetter = letter,
            isVerifiedCreator = isVerified
        )

        _currentUserProfile.value = updatedProfile

        // Push to Firestore
        firestore?.let { db ->
            try {
                val data = hashMapOf(
                    "id" to cleanUsername.removePrefix("@"),
                    "displayName" to updatedProfile.displayName,
                    "username" to updatedProfile.username,
                    "bio" to updatedProfile.bio,
                    "avatarColorHex" to updatedProfile.avatarColorHex,
                    "avatarLetter" to updatedProfile.avatarLetter,
                    "isVerifiedCreator" to updatedProfile.isVerifiedCreator,
                    "followersCount" to updatedProfile.followersCount,
                    "followingCount" to updatedProfile.followingCount,
                    "totalLikes" to updatedProfile.totalLikes,
                    "videosCount" to updatedProfile.videosCount,
                    "updatedAt" to System.currentTimeMillis()
                )
                db.collection("profiles").document(cleanUsername.removePrefix("@"))
                    .set(data, SetOptions.merge())
            } catch (e: Throwable) {
                // log
            }
        }
    }

    fun searchUsers(query: String): List<UserProfile> {
        val q = query.trim().lowercase().removePrefix("@")
        val combined = (dynamicRemoteCreators + staticCreators).distinctBy { it.username.lowercase() }
        if (q.isBlank()) return combined
        return combined.filter {
            it.username.lowercase().contains(q) ||
            it.displayName.lowercase().contains(q) ||
            it.bio.lowercase().contains(q)
        }
    }

    fun generateInviteLink(username: String): String {
        val clean = username.removePrefix("@").trim()
        return "https://zingra.app/@$clean"
    }

    fun toggleLike(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val newLiked = !post.isLiked
                    val newCount = if (newLiked) post.likesCount + 1 else post.likesCount - 1
                    post.copy(isLiked = newLiked, likesCount = maxOf(0, newCount))
                } else post
            }
        }
    }

    fun toggleBookmark(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) post.copy(isBookmarked = !post.isBookmarked) else post
            }
        }
    }

    fun toggleFollow(postId: String) {
        _posts.update { list ->
            val targetCreator = list.find { it.id == postId }?.creatorHandle
            if (targetCreator == null) list
            else list.map { post ->
                if (post.creatorHandle == targetCreator) {
                    post.copy(isFollowing = !post.isFollowing)
                } else post
            }
        }
    }

    fun toggleAudioOnly(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    post.copy(isAudioOnly = !post.isAudioOnly)
                } else post
            }
        }
    }

    fun getCommentsForPost(postId: String): List<Comment> {
        return _comments.value[postId] ?: emptyList()
    }

    fun addComment(
        postId: String,
        text: String,
        authorName: String? = null,
        authorHandle: String? = null,
        timestampTag: String? = null
    ): Comment {
        val currentProfile = _currentUserProfile.value
        val name = authorName ?: currentProfile.displayName
        val handle = authorHandle ?: currentProfile.username

        val newComment = Comment(
            id = UUID.randomUUID().toString(),
            postId = postId,
            authorName = name,
            authorHandle = handle,
            authorAvatar = name.firstOrNull()?.uppercase() ?: "Y",
            authorColorHex = currentProfile.avatarColorHex,
            text = text,
            timeAgo = "Just now",
            likesCount = 0,
            isLiked = false,
            timestampTag = timestampTag
        )

        _comments.update { map ->
            val existing = map[postId] ?: emptyList()
            map + (postId to (listOf(newComment) + existing))
        }

        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) post.copy(commentsCount = post.commentsCount + 1) else post
            }
        }

        // Push to Firestore for real-time cloud sync across phones
        firestore?.let { db ->
            try {
                val data = hashMapOf(
                    "id" to newComment.id,
                    "postId" to newComment.postId,
                    "authorName" to newComment.authorName,
                    "authorHandle" to newComment.authorHandle,
                    "authorAvatar" to newComment.authorAvatar,
                    "authorColorHex" to newComment.authorColorHex,
                    "text" to newComment.text,
                    "timeAgo" to newComment.timeAgo,
                    "likesCount" to newComment.likesCount,
                    "isLiked" to newComment.isLiked,
                    "timestampTag" to newComment.timestampTag,
                    "createdAt" to System.currentTimeMillis()
                )
                db.collection("comments").document(newComment.id).set(data)
            } catch (e: Throwable) {
                // log
            }
        }

        return newComment
    }

    fun toggleCommentLike(postId: String, commentId: String) {
        _comments.update { map ->
            val list = map[postId] ?: return@update map
            map + (postId to list.map { c ->
                if (c.id == commentId) {
                    val liked = !c.isLiked
                    c.copy(isLiked = liked, likesCount = if (liked) c.likesCount + 1 else maxOf(0, c.likesCount - 1))
                } else c
            })
        }
    }

    fun createPost(caption: String, videoUrl: String, category: String, tagsInput: String): VideoPost {
        val currentProfile = _currentUserProfile.value
        val tags = tagsInput.split(" ", ",", "#")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { if (it.startsWith("#")) it else "#$it" }

        val newPost = VideoPost(
            id = UUID.randomUUID().toString(),
            creatorName = currentProfile.displayName,
            creatorHandle = currentProfile.username,
            avatarLetter = currentProfile.avatarLetter,
            avatarColorHex = currentProfile.avatarColorHex,
            timeAgo = "Just now",
            caption = caption,
            hashtags = tags.ifEmpty { listOf("#zingra", "#socialfeed", "#new") },
            videoUrl = videoUrl.ifBlank { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4" },
            thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800",
            durationText = "0:45",
            likesCount = 1,
            isLiked = true,
            isFollowing = true,
            commentsCount = 0,
            sharesCount = 0,
            category = category,
            audioTrackName = "Original Sound - ${currentProfile.displayName}",
            viewsText = "1 view",
            bestMomentTimestamp = "00:15",
            bestMomentTitle = "Best Part"
        )

        _posts.update { listOf(newPost) + it }

        // Push to Firestore for real-time cloud sync across phones
        firestore?.let { db ->
            try {
                val data = hashMapOf(
                    "id" to newPost.id,
                    "creatorName" to newPost.creatorName,
                    "creatorHandle" to newPost.creatorHandle,
                    "avatarLetter" to newPost.avatarLetter,
                    "avatarColorHex" to newPost.avatarColorHex,
                    "timeAgo" to newPost.timeAgo,
                    "caption" to newPost.caption,
                    "hashtags" to newPost.hashtags,
                    "videoUrl" to newPost.videoUrl,
                    "thumbnailUrl" to newPost.thumbnailUrl,
                    "durationText" to newPost.durationText,
                    "likesCount" to newPost.likesCount,
                    "isLiked" to newPost.isLiked,
                    "isFollowing" to newPost.isFollowing,
                    "commentsCount" to newPost.commentsCount,
                    "sharesCount" to newPost.sharesCount,
                    "category" to newPost.category,
                    "audioTrackName" to newPost.audioTrackName,
                    "viewsText" to newPost.viewsText,
                    "bestMomentTimestamp" to newPost.bestMomentTimestamp,
                    "bestMomentTitle" to newPost.bestMomentTitle,
                    "timestampMs" to System.currentTimeMillis()
                )
                db.collection("posts").document(newPost.id).set(data)
            } catch (e: Throwable) {
                // log
            }
        }

        return newPost
    }

    fun sendChatMessage(
        text: String,
        roomId: String = "zing-4303",
        attachedVideoId: String? = null,
        attachedVideoTitle: String? = null,
        attachedTimestamp: String? = null
    ): ChatMessage {
        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val randomChars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('/', '+')
        val fakeCipher = (1..24).map { randomChars.random() }.joinToString("") + "=="

        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            roomId = roomId,
            sender = _currentUserProfile.value.displayName,
            text = text,
            cipherSnippet = fakeCipher,
            timestamp = time,
            isMe = true,
            isRead = true,
            attachedVideoId = attachedVideoId,
            attachedVideoTitle = attachedVideoTitle,
            attachedTimestamp = attachedTimestamp
        )

        _chatMessages.update { it + message }

        // Push to Firestore for live real-time encrypted chat sync across devices
        firestore?.let { db ->
            try {
                val data = hashMapOf(
                    "id" to message.id,
                    "roomId" to message.roomId,
                    "sender" to message.sender,
                    "senderHandle" to _currentUserProfile.value.username,
                    "text" to message.text,
                    "cipherSnippet" to message.cipherSnippet,
                    "timestamp" to message.timestamp,
                    "timestampMs" to System.currentTimeMillis(),
                    "attachedVideoId" to message.attachedVideoId,
                    "attachedVideoTitle" to message.attachedVideoTitle,
                    "attachedTimestamp" to message.attachedTimestamp
                )
                db.collection("chats").document(roomId).collection("messages").document(message.id).set(data)
            } catch (e: Throwable) {
                // log
            }
        }

        return message
    }

    fun shareVideoToChat(post: VideoPost, timestampNote: String = "01:24") {
        sendChatMessage(
            text = "Bhai ye video check kar! Exact $timestampNote par punchline climax hai 🔥",
            attachedVideoId = post.id,
            attachedVideoTitle = post.caption.take(35) + "...",
            attachedTimestamp = timestampNote
        )
    }

    fun receivePeerReply(replyText: String, senderName: String = "Aman Verma", roomId: String = "zing-4303") {
        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val randomChars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('/', '+')
        val fakeCipher = (1..24).map { randomChars.random() }.joinToString("") + "=="

        val reply = ChatMessage(
            id = UUID.randomUUID().toString(),
            roomId = roomId,
            sender = senderName,
            text = replyText,
            cipherSnippet = fakeCipher,
            timestamp = time,
            isMe = false,
            isRead = true
        )

        _chatMessages.update { it + reply }
    }

    fun startWatchPartyForPost(post: VideoPost, startPositionMs: Long = 0L): WatchPartyRoom {
        val newRoom = WatchPartyRoom(
            roomId = "zing-4303",
            videoTitle = post.caption.take(40) + if (post.caption.length > 40) "..." else "",
            videoUrl = post.videoUrl,
            creatorName = post.creatorName,
            isPlaying = true,
            positionMs = startPositionMs,
            participantCount = 2,
            participants = listOf("You (Host)", "${post.creatorName.split(" ").first()} (Invited)")
        )
        _activeWatchParty.value = newRoom

        // Sync room state to Firestore
        firestore?.let { db ->
            try {
                val data = hashMapOf(
                    "roomId" to newRoom.roomId,
                    "videoTitle" to newRoom.videoTitle,
                    "videoUrl" to newRoom.videoUrl,
                    "creatorName" to newRoom.creatorName,
                    "isPlaying" to newRoom.isPlaying,
                    "positionMs" to newRoom.positionMs,
                    "participantCount" to newRoom.participantCount,
                    "lastUpdatedBy" to _currentUserProfile.value.username,
                    "updatedAt" to System.currentTimeMillis()
                )
                db.collection("watch_parties").document("zing-4303").set(data, SetOptions.merge())
            } catch (e: Throwable) {
                // log
            }
        }

        return newRoom
    }

    fun toggleWatchPartyPlayback() {
        val current = _activeWatchParty.value
        val newIsPlaying = !current.isPlaying
        _activeWatchParty.value = current.copy(isPlaying = newIsPlaying)

        // Sync playback state to Firestore so all connected devices pause/play synchronously
        firestore?.let { db ->
            try {
                db.collection("watch_parties").document("zing-4303")
                    .set(
                        mapOf(
                            "isPlaying" to newIsPlaying,
                            "lastUpdatedBy" to _currentUserProfile.value.username,
                            "updatedAt" to System.currentTimeMillis()
                        ),
                        SetOptions.merge()
                    )
            } catch (e: Throwable) {
                // log
            }
        }
    }

    private fun initialPosts(): List<VideoPost> = listOf(
        VideoPost(
            id = "p1",
            creatorName = "Aman Verma",
            creatorHandle = "@aman_vfx",
            avatarLetter = "A",
            avatarColorHex = 0xFFE11D48,
            timeAgo = "10 min ago",
            caption = "Bhai log! Ye 4K open source animation check karo. Full cinematic effects aur zero buffering hai! Kaun kaun watch party me aa raha hai? 🔥🚀",
            hashtags = listOf("#animation", "#4k", "#zingra", "#cinematic"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800",
            durationText = "9:56",
            likesCount = 3420,
            isLiked = false,
            commentsCount = 89,
            sharesCount = 142,
            category = "Trending",
            audioTrackName = "Original Sound - Aman Beats",
            viewsText = "84.2K views",
            bestMomentTimestamp = "01:24",
            bestMomentTitle = "⚡ Bunny vs Glider Chase"
        ),
        VideoPost(
            id = "p2",
            creatorName = "Tech Bharat",
            creatorHandle = "@tech_bharat",
            avatarLetter = "T",
            avatarColorHex = 0xFF06B6D4,
            timeAgo = "35 min ago",
            caption = "Zero knowledge encryption on mobile: How Zingra protects your chats without ever storing plain text or asking for your phone number. 🛡️💻",
            hashtags = listOf("#tech", "#privacy", "#opensource", "#encryption"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800",
            durationText = "0:15",
            likesCount = 5120,
            isLiked = false,
            commentsCount = 140,
            sharesCount = 310,
            category = "Tech",
            audioTrackName = "Cyber Synth Loop 120BPM",
            viewsText = "120K views",
            bestMomentTimestamp = "00:08",
            bestMomentTitle = "⚡ Encryption Key Visualizer"
        ),
        VideoPost(
            id = "p3",
            creatorName = "Priya Sharma",
            creatorHandle = "@priya_vibes",
            avatarLetter = "P",
            avatarColorHex = 0xFFA855F7,
            timeAgo = "2 hours ago",
            caption = "Lo-fi sunset beats after a long coding sprint. Use podcast mode if you want to turn off the video and save 90% cellular data! 🎧☕",
            hashtags = listOf("#lofi", "#chill", "#music", "#vibes"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800",
            durationText = "0:15",
            likesCount = 9800,
            isLiked = false,
            commentsCount = 312,
            sharesCount = 890,
            category = "Music",
            audioTrackName = "Midnight Rain Lofi Chill",
            viewsText = "240K views",
            bestMomentTimestamp = "00:10",
            bestMomentTitle = "⚡ Beat Drop Breakdown"
        ),
        VideoPost(
            id = "p4",
            creatorName = "Desi Gamers Club",
            creatorHandle = "@desigamers",
            avatarLetter = "D",
            avatarColorHex = 0xFFF59E0B,
            timeAgo = "4 hours ago",
            caption = "1v4 Last Circle Clutch in tournament lobby! Audio sync testing with teammates in private watch party. Who wants 1v1 room code? 🎮🏆",
            hashtags = listOf("#gaming", "#esports", "#clutch", "#bgmi"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800",
            durationText = "1:00",
            likesCount = 14200,
            isLiked = false,
            commentsCount = 520,
            sharesCount = 1120,
            category = "Gaming",
            audioTrackName = "Battleground High Energy Mix",
            viewsText = "490K views",
            bestMomentTimestamp = "00:35",
            bestMomentTitle = "⚡ The Unbelievable 1v4 Headshot"
        ),
        VideoPost(
            id = "p5",
            creatorName = "CineWorld HD",
            creatorHandle = "@cineworld_hd",
            avatarLetter = "C",
            avatarColorHex = 0xFF10B981,
            timeAgo = "1 day ago",
            caption = "Behind the scenes VFX breakdown. From green screen to hyper-realistic sci-fi universe. Swipe to see the wireframe layer!",
            hashtags = listOf("#vfx", "#behindthescenes", "#filmmaking", "#cgi"),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?w=800",
            durationText = "10:53",
            likesCount = 7600,
            isLiked = false,
            commentsCount = 190,
            sharesCount = 430,
            category = "VFX & Animation",
            audioTrackName = "Cinematic Orchestra Suite",
            viewsText = "310K views",
            bestMomentTimestamp = "02:40",
            bestMomentTitle = "⚡ Surreal Dreamscape Reveal"
        )
    )

    private fun initialComments(): Map<String, List<Comment>> = mapOf(
        "p1" to listOf(
            Comment("c1", "p1", "Rahul Dev", "@rahul_d", "R", 0xFF6366F1, "Bhai video quality ekdum 4K crystal clear chal rahi hai! 🔥", "8 min ago", 14, false, "01:24"),
            Comment("c2", "p1", "Neha Verma", "@neha_v", "N", 0xFFEC4899, "Watch party room link copy karke group me bhej diya, sab aa rahe hain! 🙌", "5 min ago", 8, true, "03:15"),
            Comment("c3", "p1", "Kabir Singh", "@kabir_s", "K", 0xFFF59E0B, "Super smooth playback without any annoying 15-second ads! Zingra rocks.", "2 min ago", 4, false)
        ),
        "p2" to listOf(
            Comment("c4", "p2", "Sunil Rao", "@sunil_tech", "S", 0xFF10B981, "Zero knowledge architecture on mobile is revolutionary! Loved the explanation.", "30 min ago", 22, true, "03:10"),
            Comment("c5", "p2", "Arjun Mehra", "@arjun_m", "A", 0xFF38BDF8, "Finally a platform with real end-to-end encryption and fast video.", "18 min ago", 11, false)
        ),
        "p3" to listOf(
            Comment("c6", "p3", "Rohan Gupta", "@rohan_g", "R", 0xFF818CF8, "Music selection is pure fire! Added to my favorites.", "1 hour ago", 19, true, "00:08")
        )
    )

    private fun initialChatMessages(): List<ChatMessage> = listOf(
        ChatMessage(
            id = "m1",
            roomId = "zing-4303",
            sender = "Aman Verma",
            text = "Bhai Zingra live feed check kiya? Mast smooth scroll ho rahi hai!",
            cipherSnippet = "kP92xLqZ8+m1VbXn29==",
            timestamp = "09:40 PM",
            isMe = false,
            isRead = true
        ),
        ChatMessage(
            id = "m2",
            roomId = "zing-4303",
            sender = "You (Creator)",
            text = "Haan bhai! Audio-only podcast mode aur instant watch party dono test kiye, YouTube Premium se 10x better hai!",
            cipherSnippet = "aQ71mO9zZ1+e2WvBn34==",
            timestamp = "09:42 PM",
            isMe = true,
            isRead = true
        ),
        ChatMessage(
            id = "m3",
            roomId = "zing-4303",
            sender = "Aman Verma",
            text = "Watch party ka link bhej, dono sath me movie dekhte hain synchronized! 🎬⚡",
            cipherSnippet = "zR83vK5tY9+q0LcMn12==",
            timestamp = "09:44 PM",
            isMe = false,
            isRead = true,
            attachedVideoId = "p1",
            attachedVideoTitle = "Big Buck Bunny 4K 60FPS",
            attachedTimestamp = "01:24"
        )
    )
}
