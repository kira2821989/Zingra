package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ZingraViewModel
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.CreatePostDialog
import com.example.ui.components.SocialPostCard
import com.example.ui.components.UserSearchDialog
import com.example.ui.theme.ZingraBackground
import com.example.ui.theme.ZingraBorder
import com.example.ui.theme.ZingraCyan
import com.example.ui.theme.ZingraIndigo
import com.example.ui.theme.ZingraSurface
import com.example.ui.theme.ZingraSurfaceVariant
import com.example.ui.theme.ZingraTextMuted
import com.example.ui.theme.ZingraTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(
    viewModel: ZingraViewModel,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.posts.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val activeCommentPost by viewModel.activeCommentPost.collectAsState()
    val activeComments by viewModel.activeComments.collectAsState()
    val showCreateDialog by viewModel.showCreatePostDialog.collectAsState()
    val toastNotification by viewModel.toastNotification.collectAsState()
    val showUserSearch by viewModel.showUserSearchDialog.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val currentUserProfile by viewModel.currentUserProfile.collectAsState()
    val isCloudSync by viewModel.isCloudSyncConnected.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val categories = listOf("All", "Trending", "Tech", "Gaming", "Music", "VFX & Animation")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ZingraBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 1. Top App Bar with Branding and Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZingraSurface)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Zingra Logo with Lightning Accent
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(ZingraCyan, ZingraIndigo))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Zingra Logo",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Zingra",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AD-FREE",
                            color = Color(0xFF10B981),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isCloudSync) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF06B6D4).copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF06B6D4))
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "SYNC LIVE",
                                    color = Color(0xFF06B6D4),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Search Creators & Handles Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ZingraSurfaceVariant)
                            .clickable { viewModel.openUserSearch() }
                            .testTag("feed_search_users_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Creators",
                            tint = ZingraCyan,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // KILLER UNIQUE FEATURE: Ghost Shield Camouflage Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1E293B))
                            .clickable { viewModel.toggleStealthMode() }
                            .padding(horizontal = 9.dp, vertical = 6.dp)
                            .testTag("stealth_toggle_top"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Ghost Camouflage",
                                tint = ZingraCyan,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Ghost",
                                color = ZingraCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Post button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(ZingraCyan)
                            .clickable { viewModel.openCreatePost() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("create_post_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create Post",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Post",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. Category Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZingraSurface)
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) Color.White else ZingraSurfaceVariant
                            )
                            .clickable { viewModel.selectCategory(cat) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("category_chip_$cat"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else ZingraTextSecondary
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ZingraBorder)
            )

            // 3. Social Video Feed List (Instagram / YouTube / Facebook Style)
            if (posts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No videos in this category yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = ZingraTextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap + Post to publish the first video!",
                            fontSize = 13.sp,
                            color = ZingraTextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("social_feed_list"),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(posts, key = { it.id }) { post ->
                        SocialPostCard(
                            post = post,
                            onLikeClick = { viewModel.toggleLike(post.id) },
                            onCommentClick = { viewModel.openComments(post) },
                            onBookmarkClick = { viewModel.toggleBookmark(post.id) },
                            onFollowClick = { viewModel.toggleFollow(post.id) },
                            onWatchPartyClick = { viewModel.startWatchParty(post) },
                            onToggleAudioOnly = { viewModel.toggleAudioOnly(post.id) },
                            onJumpToBestMoment = { viewModel.jumpToBestMoment(post) },
                            onShareToSecretChat = { viewModel.shareVideoToSecretChat(post) }
                        )
                    }
                }
            }
        }

        // Floating Animated Notification Toast Banner
        AnimatedVisibility(
            visible = toastNotification != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.95f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = toastNotification ?: "",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Floating Action Button to quickly create/publish a video post
        FloatingActionButton(
            onClick = { viewModel.openCreatePost() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 84.dp)
                .testTag("fab_create_post"),
            containerColor = ZingraCyan,
            contentColor = Color.Black,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New Post"
            )
        }

        // Comments Bottom Sheet
        if (activeCommentPost != null) {
            CommentsBottomSheet(
                post = activeCommentPost!!,
                comments = activeComments,
                sheetState = sheetState,
                onDismiss = { viewModel.closeComments() },
                onAddComment = { text ->
                    viewModel.addComment(activeCommentPost!!.id, text)
                },
                onToggleCommentLike = { commentId ->
                    viewModel.toggleCommentLike(activeCommentPost!!.id, commentId)
                }
            )
        }

        // Create Post Dialog
        if (showCreateDialog) {
            CreatePostDialog(
                onDismiss = { viewModel.closeCreatePost() },
                onSubmitPost = { caption, url, cat, tags ->
                    viewModel.createPost(caption, url, cat, tags)
                }
            )
        }

        // Search Creators & Handles Dialog
        if (showUserSearch) {
            UserSearchDialog(
                searchQuery = searchQuery,
                searchResults = searchResults,
                currentUserProfile = currentUserProfile,
                inviteLink = viewModel.generateInviteLink(),
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onDismiss = { viewModel.closeUserSearch() },
                onStartChat = { user -> viewModel.startChatWithUser(user) },
                onShowToast = { msg -> viewModel.showToast(msg) }
            )
        }
    }
}
