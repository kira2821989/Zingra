# Real-Time Multi-Device Cloud Sync & Cross-Phone Testing Architecture

Enable real-time cloud synchronization for Zingra so two or more physical Android phones can interact live across all features: publishing video posts, reacting with comments, syncing Watch Party playback, and chatting in end-to-end encrypted rooms.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> **Confirmed Choices from Phase 1:**
> - **Multi-Device Testing Strategy**: Cloud sync backend (Firebase Firestore real-time listeners) to allow instantaneous two-way synchronization between two separate physical phones.
> - **Feature Scope**: All core features included (Social Feed posts, live comments, synchronized Watch Party playback, and private encrypted chat).

---

### 1. Overview & Core Concept

Currently, Zingra maintains an in-memory repository state on each instance. While ideal for single-device prototyping, testing between two separate phones requires a shared cloud state where changes made on **Phone 1** (e.g., publishing a video post, playing/pausing a Watch Party, or sending a chat message) instantly update on **Phone 2** via real-time WebSocket listeners.

#### How Testing on Two Phones Works:
1. **Device 1 (e.g. Phone A)** installs Zingra APK or opens the shared preview stream. User sets handle as `@creator_alpha`.
2. **Device 2 (e.g. Phone B)** installs Zingra APK or opens in a second browser tab. User sets handle as `@creator_beta`.
3. **Live Sync Actions**:
   - **Watch Party Sync**: Phone A pauses or seeks video in room `zing-4303`; Phone B's player instantly pauses and syncs to the exact millisecond timestamp.
   - **Encrypted Chat**: Phone A sends a secure message; Phone B receives the message in real-time with double checkmarks.
   - **Feed & Comments**: Phone A creates a post or comments on a video; Phone B sees the new post and comment badge without pulling to refresh.

---

### 2. User Experience & Visual Design

#### Multi-Device Sync Indicators
- **Live Connection Badge**: A subtle green pulsing pill (`● Cloud Connected`) on the Top Bar indicating active real-time Firestore listeners.
- **Watch Party Peer Sync Status**: Shows connected peers in the room (e.g. `2 Devices Synced • Phone A (Host) & Phone B`).
- **Instant Comment Floating Badge**: When another device comments, a subtle notification badge slides in smoothly.
- **Cross-Device Profile Search**: Phone B can search `@creator_alpha` in the search dialog and immediately discover Phone A.

---

### 3. Key Product Decisions & Trade-Offs

| Decision | Chosen Approach | Why | Alternatives Considered |
|---|---|---|---|
| **Real-time Backend** | **Firebase Firestore (`addSnapshotListener`)** | Zero server maintenance, sub-second latency across global mobile devices, and native Android offline caching. | Custom WebSocket server (requires hosting infrastructure and maintenance) or polling REST API (high battery/data drain). |
| **Authentication Flow** | **Anonymous Device Token / Google Sign-In** | Frictionless testing on two devices without mandatory phone numbers or passwords. | SMS OTP (fails policy for privacy-first zero-phone-number design). |
| **Watch Party Latency** | **Optimistic Playback + Periodic Delta Sync** | Prevents choppy stuttering if network pings fluctuate, only syncing when drift > 500ms. | Strict lock-step frame locking (stutters on weak cellular data). |
| **Local Fallback** | **Hybrid Repository Pattern** | If internet drops, app works smoothly offline with pre-cached sample videos and local drafts. | Strict online-only (crashes if connectivity is interrupted). |

---

### 4. Technical Architecture & Data Strategy

```
  ┌───────────────────────────────────────────────────────────────┐
  │                        FIREBASE CLOUD                         │
  │                                                               │
  │   ┌──────────────┐   ┌──────────────┐   ┌─────────────────┐   │
  │   │ posts/       │   │ comments/    │   │ watch_parties/  │   │
  │   │  - id        │   │  - postId    │   │  - roomId       │   │
  │   │  - videoUrl  │   │  - text      │   │  - isPlaying    │   │
  │   │  - creator   │   │  - author    │   │  - positionMs   │   │
  │   └──────▲───────┘   └──────▲───────┘   └────────▲────────┘   │
  │          │                  │                    │            │
  └──────────┼──────────────────┼────────────────────┼────────────┘
             │ real-time        │ real-time          │ real-time
             │ snapshots        │ snapshots          │ snapshots
      ┌──────▼──────────────────▼────────────────────▼──────┐
      │                                                     │
 ┌────┴────────────────────────┐       ┌────────────────────┴────┐
 │  PHONE 1: @creator_alpha    │       │  PHONE 2: @creator_beta │
 │                             │       │                         │
 │  - Local Compose UI         │       │  - Local Compose UI     │
 │  - ZingraRepository         │       │  - ZingraRepository     │
 │  - ExoPlayer / VideoView    │       │  - ExoPlayer / VideoView│
 └─────────────────────────────┘       └─────────────────────────┘
```

#### Firestore Collections Schema
1. **`posts`**: `id`, `creatorName`, `creatorHandle`, `caption`, `videoUrl`, `category`, `likesCount`, `createdAt`.
2. **`comments`**: `id`, `postId`, `authorName`, `authorHandle`, `text`, `timestampTag`, `createdAt`.
3. **`watch_parties`**: `roomId` (`zing-4303`), `videoUrl`, `videoTitle`, `isPlaying`, `positionMs`, `lastUpdatedBy`, `updatedAt`.
4. **`chats`**: `roomId`, `messages` subcollection (`id`, `sender`, `text`, `cipherSnippet`, `timestamp`, `isMe`).
5. **`profiles`**: `username`, `displayName`, `bio`, `avatarColorHex`, `isVerified`.

---

### 5. Step-by-Step Implementation Steps

1. **Step 1: Firebase Provisioning (`ProvisionFirebase`)**:
   - Extract local debug keystore SHA-1 / SHA-256 fingerprints.
   - Show user Firebase consent UI via `set_up_firebase` / `show_aistudio_ui`.
   - Provision Firestore database for package `com.aistudio.zingra.xkvrmq`.
2. **Step 2: Firestore Client Integration**:
   - Add Firestore SDK dependencies to `app/build.gradle.kts`.
   - Setup `FirebaseFirestore.getInstance()` in `ZingraRepository`.
3. **Step 3: Real-Time Snapshot Listeners**:
   - Replace in-memory static mutations with Firestore snapshot listeners for posts, comments, watch party room state, and chat messages.
4. **Step 4: Two-Phone Testing & Installation Guide**:
   - Provide direct steps to download/install the APK on two Android phones using the AI Studio Export / APK build action, or test simultaneously using Dev Preview URL + Shared Preview URL in two browser windows.
