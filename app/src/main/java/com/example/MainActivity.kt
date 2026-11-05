package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ZingraTab
import com.example.ui.ZingraViewModel
import com.example.ui.components.GhostCamouflageView
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SocialFeedScreen
import com.example.ui.screens.WatchPartyScreen
import com.example.ui.screens.ZingraChatScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ZingraBackground
import com.example.ui.theme.ZingraBorder
import com.example.ui.theme.ZingraCyan
import com.example.ui.theme.ZingraSurface
import com.example.ui.theme.ZingraTextMuted

class MainActivity : ComponentActivity() {

    private val viewModel: ZingraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ZingraApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ZingraApp(viewModel: ZingraViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isStealthMode by viewModel.isStealthMode.collectAsState()

    // Handle system back navigation:
    // 1. If in stealth mode, exit stealth mode
    // 2. Otherwise return to FEED if in another tab
    BackHandler(enabled = isStealthMode || currentTab != ZingraTab.FEED) {
        if (isStealthMode) {
            viewModel.toggleStealthMode()
        } else {
            viewModel.selectTab(ZingraTab.FEED)
        }
    }

    if (isStealthMode) {
        // KILLER UNIQUE FEATURE: Ghost Shield Camouflage View
        GhostCamouflageView(
            onExitStealth = { viewModel.toggleStealthMode() }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ZingraBackground,
            bottomBar = {
                ZingraBottomNavigation(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    ZingraTab.FEED -> SocialFeedScreen(viewModel = viewModel)
                    ZingraTab.WATCH_PARTY -> WatchPartyScreen(viewModel = viewModel)
                    ZingraTab.CHAT -> ZingraChatScreen(viewModel = viewModel)
                    ZingraTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ZingraBottomNavigation(
    currentTab: ZingraTab,
    onTabSelected: (ZingraTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ZingraSurface)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ZingraBorder)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = if (currentTab == ZingraTab.FEED) Icons.Filled.Home else Icons.Outlined.Home,
                label = "Feed",
                isSelected = currentTab == ZingraTab.FEED,
                onClick = { onTabSelected(ZingraTab.FEED) },
                testTag = "nav_tab_feed"
            )

            NavItem(
                icon = if (currentTab == ZingraTab.WATCH_PARTY) Icons.Filled.Group else Icons.Outlined.Group,
                label = "Watch Party",
                isSelected = currentTab == ZingraTab.WATCH_PARTY,
                onClick = { onTabSelected(ZingraTab.WATCH_PARTY) },
                testTag = "nav_tab_watch_party"
            )

            NavItem(
                icon = if (currentTab == ZingraTab.CHAT) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                label = "Zingra Chat",
                isSelected = currentTab == ZingraTab.CHAT,
                badgeCount = 3,
                onClick = { onTabSelected(ZingraTab.CHAT) },
                testTag = "nav_tab_chat"
            )

            NavItem(
                icon = if (currentTab == ZingraTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                label = "Profile",
                isSelected = currentTab == ZingraTab.PROFILE,
                onClick = { onTabSelected(ZingraTab.PROFILE) },
                testTag = "nav_tab_profile"
            )
        }
    }
}

@Composable
fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) ZingraCyan else ZingraTextMuted,
                modifier = Modifier.size(24.dp)
            )

            if (badgeCount > 0 && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ZingraCyan)
                )
            }
        }

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ZingraCyan else ZingraTextMuted,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
