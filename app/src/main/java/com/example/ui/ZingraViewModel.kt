package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ZingraRepository
import com.example.model.ChatMessage
import com.example.model.Comment
import com.example.model.UserProfile
import com.example.model.VideoPost
import com.example.model.WatchPartyRoom
import com.example.model.ZingraTab
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ZingraViewModel(
    private val repository: ZingraRepository = ZingraRepository()
) : ViewModel() {

    private val _currentTab = MutableStateFlow(ZingraTab.FEED)
    val currentTab: StateFlow<ZingraTab> = _currentTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _activeCommentPost = MutableStateFlow<VideoPost?>(null)
    val activeCommentPost: StateFlow<VideoPost?> = _activeCommentPost.asStateFlow()

    private val _showCreatePostDialog = MutableStateFlow(false)
    val showCreatePostDialog: StateFlow<Boolean> = _showCreatePostDialog.asStateFlow()

    // User Profile & Unique Username System
    val currentUserProfile: StateFlow<UserProfile> = repository.currentUserProfile

    private val _showEditProfileDialog = MutableStateFlow(false)
    val showEditProfileDialog: StateFlow<Boolean> = _showEditProfileDialog.asStateFlow()

    private val _showUserSearchDialog = MutableStateFlow(false)
    val showUserSearchDialog: StateFlow<Boolean> = _showUserSearchDialog.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<UserProfile>> = _searchQuery
        .combine(repository.currentUserProfile) { query, _ ->
            repository.searchUsers(query)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.searchUsers(""))

    // Ghost Shield / Stealth Camouflage Mode
    private val _isStealthMode = MutableStateFlow(false)
    val isStealthMode: StateFlow<Boolean> = _isStealthMode.asStateFlow()

    // Temporary toast or snackbar notification banner
    private val _toastNotification = MutableStateFlow<String?>(null)
    val toastNotification: StateFlow<String?> = _toastNotification.asStateFlow()

    // Real-Time Cloud Sync State for Multi-Phone Testing
    val isCloudSyncConnected: StateFlow<Boolean> = repository.isCloudSyncConnected

    val posts: StateFlow<List<VideoPost>> = combine(
        repository.posts,
        _selectedCategory
    ) { allPosts, category ->
        if (category == "All") allPosts
        else allPosts.filter { it.category.equals(category, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.posts.value)

    val activeComments: StateFlow<List<Comment>> = combine(
        repository.comments,
        _activeCommentPost
    ) { allComments, post ->
        if (post == null) emptyList()
        else allComments[post.id] ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeWatchParty: StateFlow<WatchPartyRoom> = repository.activeWatchParty
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages

    fun selectTab(tab: ZingraTab) {
        _currentTab.value = tab
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleStealthMode() {
        _isStealthMode.update { !it }
    }

    fun showToast(msg: String) {
        _toastNotification.value = msg
        viewModelScope.launch {
            delay(2800)
            if (_toastNotification.value == msg) {
                _toastNotification.value = null
            }
        }
    }

    fun openEditProfile() {
        _showEditProfileDialog.value = true
    }

    fun closeEditProfile() {
        _showEditProfileDialog.value = false
    }

    fun openUserSearch() {
        _showUserSearchDialog.value = true
    }

    fun closeUserSearch() {
        _showUserSearchDialog.value = false
        _searchQuery.value = ""
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateProfile(
        displayName: String,
        username: String,
        bio: String,
        avatarColorHex: Long,
        isVerified: Boolean
    ) {
        repository.updateProfile(displayName, username, bio, avatarColorHex, isVerified)
        closeEditProfile()
        showToast("Profile updated! Handle is now ${currentUserProfile.value.username} ✨")
    }

    fun generateInviteLink(): String {
        return repository.generateInviteLink(currentUserProfile.value.username)
    }

    fun startChatWithUser(user: UserProfile) {
        closeUserSearch()
        selectTab(ZingraTab.CHAT)
        showToast("Started encrypted chat with ${user.username} 🔒")
    }

    fun toggleLike(postId: String) {
        repository.toggleLike(postId)
    }

    fun toggleBookmark(postId: String) {
        repository.toggleBookmark(postId)
    }

    fun toggleFollow(postId: String) {
        repository.toggleFollow(postId)
    }

    fun toggleAudioOnly(postId: String) {
        repository.toggleAudioOnly(postId)
        val post = repository.posts.value.find { it.id == postId }
        if (post?.isAudioOnly == true) {
            showToast("📻 Audio Podcast Mode Active • 90% Data Saved!")
        } else {
            showToast("🎬 HD Video Stream Restored")
        }
    }

    fun shareVideoToSecretChat(post: VideoPost) {
        repository.shareVideoToChat(post, post.bestMomentTimestamp)
        showToast("⚡ Sent '${post.caption.take(20)}...' to Zingra Secret Chat!")
        _currentTab.value = ZingraTab.CHAT
    }

    fun jumpToBestMoment(post: VideoPost) {
        startWatchParty(post)
        showToast("⚡ Jumped directly to Best Part (${post.bestMomentTimestamp})!")
    }

    fun openComments(post: VideoPost) {
        _activeCommentPost.value = post
    }

    fun closeComments() {
        _activeCommentPost.value = null
    }

    fun addComment(postId: String, text: String, timestampTag: String? = null) {
        if (text.isBlank()) return
        repository.addComment(postId, text.trim(), timestampTag = timestampTag)
        showToast("💬 Comment dropped as ${currentUserProfile.value.username}!")
    }

    fun toggleCommentLike(postId: String, commentId: String) {
        repository.toggleCommentLike(postId, commentId)
    }

    fun startWatchParty(post: VideoPost, startPositionMs: Long = 0L) {
        repository.startWatchPartyForPost(post, startPositionMs)
        _currentTab.value = ZingraTab.WATCH_PARTY
    }

    fun toggleWatchPartyPlayback() {
        repository.toggleWatchPartyPlayback()
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        repository.sendChatMessage(text.trim())

        // Simulated intelligent peer response for lively interactive experience
        viewModelScope.launch {
            delay(1200)
            val peerReplies = listOf(
                "Sahi bol raha hai ${currentUserProfile.value.displayName}! Mast handle banaya hai! 🔥",
                "Maine bhi ${currentUserProfile.value.username} ko follow kar liya! 🚀",
                "Zingra invite link se sab dost aa gaye group me 👥",
                "Parda aur privacy lock dono ek number hain ⚡"
            )
            repository.receivePeerReply(peerReplies.random())
        }
    }

    fun openCreatePost() {
        _showCreatePostDialog.value = true
    }

    fun closeCreatePost() {
        _showCreatePostDialog.value = false
    }

    fun createPost(caption: String, videoUrl: String, category: String, tags: String) {
        if (caption.isBlank()) return
        repository.createPost(caption, videoUrl, category, tags)
        _showCreatePostDialog.value = false
        _currentTab.value = ZingraTab.FEED
        showToast("🚀 Video post published by ${currentUserProfile.value.username}!")
    }
}
