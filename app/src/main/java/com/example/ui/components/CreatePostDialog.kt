package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ZingraBorder
import com.example.ui.theme.ZingraCyan
import com.example.ui.theme.ZingraIndigo
import com.example.ui.theme.ZingraSurface
import com.example.ui.theme.ZingraSurfaceVariant
import com.example.ui.theme.ZingraTextMuted
import com.example.ui.theme.ZingraTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onSubmitPost: (caption: String, videoUrl: String, category: String, tags: String) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Trending") }
    var tags by remember { mutableStateOf("#zingra #viral #socialfeed") }
    var videoSource by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4") }

    val categories = listOf("Trending", "Tech", "Gaming", "Music", "VFX & Animation", "Comedy")

    val videoPresets = listOf(
        "For Bigger Blazes (Action)" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "Big Buck Bunny (Animation)" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "Tears of Steel (Sci-Fi)" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
        "For Bigger Escapes (Nature)" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ZingraSurface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Create Video Post",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Share high-quality video with the Zingra social community",
                    fontSize = 12.sp,
                    color = ZingraTextMuted,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Caption
                Text(
                    text = "Caption & Description",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZingraTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    placeholder = { Text("What's this video about? Write a catchy caption...", color = ZingraTextMuted, fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_caption_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ZingraSurfaceVariant,
                        unfocusedContainerColor = ZingraSurfaceVariant,
                        focusedBorderColor = ZingraCyan,
                        unfocusedBorderColor = ZingraBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category
                Text(
                    text = "Select Category",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZingraTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) ZingraCyan else ZingraSurfaceVariant,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Video Presets
                Text(
                    text = "Video Stream Preset",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZingraTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                videoPresets.forEach { (name, url) ->
                    val isSelected = videoSource == url
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .background(
                                if (isSelected) Color(0xFF1E293B) else ZingraSurfaceVariant,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { videoSource = url }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSelected) "● " else "○ ",
                            color = if (isSelected) ZingraCyan else ZingraTextMuted,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else ZingraTextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tags
                Text(
                    text = "Hashtags",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZingraTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    placeholder = { Text("#zingra #viral", color = ZingraTextMuted, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
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

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = ZingraTextSecondary)
                    }
                    Button(
                        onClick = {
                            if (caption.isNotBlank()) {
                                onSubmitPost(caption, videoSource, selectedCategory, tags)
                            }
                        },
                        enabled = caption.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZingraCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.testTag("submit_post_button")
                    ) {
                        Text("Post Video", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
