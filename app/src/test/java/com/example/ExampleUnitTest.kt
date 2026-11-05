package com.example

import com.example.data.ZingraRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun repositorySocialFeedOperations() {
    val repo = ZingraRepository()
    val posts = repo.posts.value
    assertTrue("Posts feed should not be empty", posts.isNotEmpty())

    val firstPost = posts.first()
    val initialLikes = firstPost.likesCount
    repo.toggleLike(firstPost.id)

    val updatedPost = repo.posts.value.first()
    assertTrue("Post should be liked", updatedPost.isLiked)
    assertEquals(initialLikes + 1, updatedPost.likesCount)

    // Test adding comment
    val comment = repo.addComment(firstPost.id, "Zingra is amazing!", timestampTag = "01:24")
    assertNotNull(comment)
    assertEquals("Zingra is amazing!", comment.text)
    assertEquals("01:24", comment.timestampTag)

    // Test killer feature: Audio-only podcast mode toggle
    assertFalse("Post should default to video", firstPost.isAudioOnly)
    repo.toggleAudioOnly(firstPost.id)
    val audioPost = repo.posts.value.first()
    assertTrue("Post should switch to audio-only mode", audioPost.isAudioOnly)

    // Test killer feature: Share video with bookmark to encrypted chat
    repo.shareVideoToChat(firstPost, "01:24")
    val lastMsg = repo.chatMessages.value.last()
    assertEquals(firstPost.id, lastMsg.attachedVideoId)
    assertEquals("01:24", lastMsg.attachedTimestamp)
    assertTrue("Should contain cipher snippet", lastMsg.cipherSnippet.isNotEmpty())
  }

  @Test
  fun uniqueUsernameAndProfileCustomization() {
    val repo = ZingraRepository()
    val initialProfile = repo.currentUserProfile.value
    assertEquals("@you_zingra", initialProfile.username)
    assertTrue(initialProfile.isVerifiedCreator)

    // Update handle and profile
    repo.updateProfile(
        displayName = "Vikram Aditya",
        username = "vikram_vfx",
        bio = "Creating 3D VFX motion graphics on Zingra!",
        avatarColorHex = 0xFFF59E0B,
        isVerified = true
    )

    val updated = repo.currentUserProfile.value
    assertEquals("Vikram Aditya", updated.displayName)
    assertEquals("@vikram_vfx", updated.username)
    assertEquals("V", updated.avatarLetter)
    assertEquals(0xFFF59E0B, updated.avatarColorHex)
    assertTrue(updated.isVerifiedCreator)

    // Verify 1-click invite link generation
    val inviteLink = repo.generateInviteLink(updated.username)
    assertEquals("https://zingra.app/@vikram_vfx", inviteLink)
  }

  @Test
  fun creatorDirectorySearchAndDiscovery() {
    val repo = ZingraRepository()

    // Search empty query returns all creators
    val allCreators = repo.searchUsers("")
    assertTrue("Creators directory should have users", allCreators.size >= 4)

    // Search by handle
    val amanResults = repo.searchUsers("aman")
    assertTrue("Should find @aman_vfx", amanResults.any { it.username == "@aman_vfx" })

    // Search by display name
    val techResults = repo.searchUsers("Bharat")
    assertTrue("Should find Tech Bharat", techResults.any { it.displayName.contains("Bharat") })

    // Search nonexistent
    val emptyResults = repo.searchUsers("xyz_nonexistent_12345")
    assertTrue("Should return empty list for nonexistent user", emptyResults.isEmpty())
  }

  @Test
  fun authorIdentityPropagatesToCommentsAndPosts() {
    val repo = ZingraRepository()
    repo.updateProfile(
        displayName = "Aarav Sharma",
        username = "@aarav_vfx",
        bio = "Motion designer",
        avatarColorHex = 0xFF10B981,
        isVerified = true
    )

    val post = repo.posts.value.first()
    val comment = repo.addComment(post.id, "Testing dynamic username linking!")
    assertEquals("Aarav Sharma", comment.authorName)
    assertEquals("@aarav_vfx", comment.authorHandle)

    repo.sendChatMessage("Hello secure chat!")
    val lastMsg = repo.chatMessages.value.last()
    assertEquals("Aarav Sharma", lastMsg.sender)
    assertTrue(lastMsg.isMe)
  }
}
