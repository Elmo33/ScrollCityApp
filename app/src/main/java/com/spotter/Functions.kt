package com.spotter

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.spotter.sampledata.Venue
import com.spotter.ui.ActivityItem

fun isVideoResource(context: android.content.Context, resId: Int): Boolean {
//    val context = LocalContext.current // Replace `App.context` with however you access your `Context`
    val resourceType = context.resources.getResourceTypeName(resId)
    println("Resource ID: $resId, Type: $resourceType")
    return resourceType == "raw" // Return true if it's a "raw" resource (indicating a video/audio file)
}

fun createExoPlayers(
    context: android.content.Context,
    venues: List<Venue>
): List<ExoPlayer> {
    return venues.map { venue ->
        ExoPlayer.Builder(context).build().apply {
            venue.contentResIds.forEach { resId ->
                if (isVideoResource(context, resId)) {  // Only add video resources
                    val mediaItem = MediaItem.fromUri(
                        "android.resource://${context.packageName}/$resId"
                    )
                    addMediaItem(mediaItem)  // Add each video media item to the player
                }
            }
            prepare()  // Prepare the player with all added media items
        }
    }
}

// Function to handle gestures for the video player
fun Modifier.handleVideoPlayerGestures(exoPlayer: ExoPlayer): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            awaitFirstDown(requireUnconsumed = false)
            val startTime = System.currentTimeMillis()
            var isLongPress = false

            while (true) {
                val event = awaitPointerEvent()
                val duration = System.currentTimeMillis() - startTime

                if (duration > 1000 && !isLongPress) {
                    isLongPress = true
                    exoPlayer.playWhenReady = false // Pause video on long press
                }

                if (event.changes.any { it.changedToUp() }) {
                    if (isLongPress) {
                        exoPlayer.playWhenReady = true // Resume video on release
                    } else {
                        exoPlayer.volume =
                            if (exoPlayer.volume > 0f) 0f else 1f // Toggle mute on short tap
                    }
                    break
                }
            }
        }
    }
}

fun getSampleActivities(): List<ActivityItem> {
    return listOf(
        ActivityItem("Carting", R.drawable.ic_home),
        ActivityItem("Clubbing", R.drawable.ic_home),
        ActivityItem("Arcade", R.drawable.ic_home),
        ActivityItem("Go-Kart", R.drawable.ic_home),
        ActivityItem("Cafe", R.drawable.ic_home),
        ActivityItem("Music", R.drawable.ic_home),
        ActivityItem("Cinema", R.drawable.ic_home),
        ActivityItem("Bowling", R.drawable.ic_home),
        ActivityItem("Bar", R.drawable.ic_home),
        ActivityItem("Park", R.drawable.ic_home),
        ActivityItem("Gallery", R.drawable.ic_home),
        ActivityItem("Museum", R.drawable.ic_home),
        ActivityItem("Restaurant", R.drawable.ic_home),
        ActivityItem("Adventure", R.drawable.ic_home),
        ActivityItem("Beach", R.drawable.ic_home),
        ActivityItem("Hiking", R.drawable.ic_home),
        ActivityItem("Pool", R.drawable.ic_home),
        ActivityItem("Gym", R.drawable.ic_home),
        ActivityItem("Escape Room", R.drawable.ic_home),
        ActivityItem("Concert", R.drawable.ic_home),
        ActivityItem("Live Show", R.drawable.ic_home),
        ActivityItem("Paintball", R.drawable.ic_home),
        ActivityItem("Festival", R.drawable.ic_home),
        ActivityItem("Theater", R.drawable.ic_home),
        ActivityItem("Comedy Club", R.drawable.ic_home)
    )
}
