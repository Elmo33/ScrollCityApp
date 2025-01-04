package com.scrollcity.ui

import android.util.Log
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.scrollcity.sampledata.Venue
import com.scrollcity.sampledata.provideSampleVenues


fun createExoPlayers(
    context: android.content.Context,
    venues: List<Venue>
): List<ExoPlayer> {
    return venues.map { venue ->
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(
                "android.resource://${context.packageName}/${venue.videoResId}"
            )
            setMediaItem(mediaItem)
            prepare()
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
                        .handleVideoPlayerGestures(currentPlayer) // Using the refactored function
                ) {
                    FullScreenMediaItem(
                        venue = venues[page],
                        exoPlayer = currentPlayer,
                        isActive = isCurrentPage
                    )
                }
            }

            // Bottom Navigation Bar with FAB
            BottomNavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                navController = navController // Pass the NavController directly
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
                        exoPlayer.volume = if (exoPlayer.volume > 0f) 0f else 1f // Toggle mute on short tap
                    }
                    break
                }
            }
        }
    }
}



@Composable
fun FullScreenMediaItem(
    venue: Venue,
    exoPlayer: ExoPlayer,
    isActive: Boolean
) {
    LaunchedEffect(isActive) {
        exoPlayer.playWhenReady = isActive
        if (isActive) exoPlayer.play() else exoPlayer.pause()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
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
    }
    // Text overlays
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top row: People + Cost
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = venue.peopleCount,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = venue.costIndicator,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.Green
            )
        }

        // Bottom text: Name, distance, description
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 110.dp),
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
