package com.spotter.ui

import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.FlowRowScopeInstance.align
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.spotter.R
import com.spotter.sampledata.Venue
import com.spotter.sampledata.provideSampleVenues

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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScrollScreen(navController: NavController) {
    val context = LocalContext.current
    val venues = remember { provideSampleVenues() }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { venues.size })

    // Create all the ExoPlayers
    val exoPlayers = remember { createExoPlayers(context, venues) }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayers.forEach { it.release() }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val isCurrentPage = (pagerState.currentPage == page)
                val currentPlayer = exoPlayers[page]

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .handleVideoPlayerGestures(currentPlayer) // Using your gesture-handing extension/function
                ) {
                    FullScreenMediaItem(
                        venue = venues[page],
                        exoPlayer = currentPlayer,
                        isActive = isCurrentPage,
                        navController= navController
                    )
                }
            }

            // Bottom Navigation Bar with FAB
            BottomNavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                navController = navController
            )
        }
    }
}


// Function to handle gestures for the video player
fun Modifier.handleVideoPlayerGestures(exoPlayer: ExoPlayer): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val down = awaitFirstDown(requireUnconsumed = false)
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullScreenMediaItem(
    venue: Venue,
    exoPlayer: ExoPlayer,
    isActive: Boolean,
    navController: NavController
) {
    val horizontalPagerState =
        rememberPagerState(initialPage = 0, pageCount = { venue.contentResIds.size })
    val context = LocalContext.current

    LaunchedEffect(isActive) {
        exoPlayer.playWhenReady = isActive && isVideoResource(
            context,
            venue.contentResIds[horizontalPagerState.currentPage]
        )
        if (isActive && isVideoResource(
                context,
                venue.contentResIds[horizontalPagerState.currentPage]
            )
        ) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = horizontalPagerState,
            modifier = Modifier.fillMaxSize()
        ) { mediaIndex ->
            val mediaResId = venue.contentResIds[mediaIndex]
            if (isVideoResource(context, mediaResId)) {
                // Video Player View
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT
                            )
                            setBackgroundColor(android.graphics.Color.BLACK)
                            player = exoPlayer
                            useController = false
                        }
                    }
                )
            } else {
                // Image View
                androidx.compose.foundation.Image(
                    painter = painterResource(id = mediaResId),
                    contentDescription = "Image content",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Media Indicator (lines at the top)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 30.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(venue.contentResIds.size) { index ->
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(4.dp)
                        .background(
                            if (horizontalPagerState.currentPage == index) Color.White else Color.Gray,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                if (index != venue.contentResIds.size - 1) {
                    Spacer(modifier = Modifier.width(12.dp)) // Adds spacing between indicators, but not after the last one
                }
            }
        }

        // Venue details (name, distance, description)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
//            Spacer(modifier = Modifier.weight(1f)) // Pushes the inner content to the bottom

            // Bottom-right aligned content
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Column(

                ) {
                    IconButton(
                        onClick = {
                            navController.navigate("filterActivities")
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune, // Material icon that looks like sliders
                            contentDescription = "Open Filters",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_heart),
                            contentDescription = "label",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share),
                            contentDescription = "label",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                }

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 110.dp)
            ) {
                Text(
                    text = venue.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    color = Color.White
                )
                Text(
                    text = venue.distance,
                    fontSize = 24.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = venue.description,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
        }
    }
}


fun isVideoResource(context: android.content.Context, resId: Int): Boolean {
//    val context = LocalContext.current // Replace `App.context` with however you access your `Context`
    val resourceType = context.resources.getResourceTypeName(resId)
    println("Resource ID: $resId, Type: $resourceType")
    return resourceType == "raw" // Return true if it's a "raw" resource (indicating a video/audio file)
}
