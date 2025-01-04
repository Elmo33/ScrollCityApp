package com.scrollcity.ui

// --- Android ---
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout

// --- Compose Foundation & UI ---
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex

// --- Pointer input extension functions (Compose UI) ---
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

// --- Media3 (ExoPlayer) ---
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

// --- Coroutines ---
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.TimeoutCancellationException

// --- Your local data/model ---
import com.scrollcity.R


data class Venue(
    val name: String,
    val distance: String,
    val description: String,
    val peopleCount: String,
    val costIndicator: String,
    val videoResId: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScrollScreen() {
    val context = LocalContext.current

    // Sample data
    val venues = listOf(
        Venue("Joe mama carting", "12 kms away", "Joe mama loves carting.", "5 People", "$$", R.raw.testvid1),
        Venue("Adventure Park",   "5 kms away",  "An outdoor park perfect for thrill-seekers!", "4+ People", "Free", R.raw.testvid2),
        Venue("Jazz Club",        "8 kms away",  "Enjoy live jazz with cocktails in this club.", "3 People", "$$$", R.raw.testvid3),
        Venue("Sunset Cafe",      "3 kms away",  "The best place for morning coffee with a view.", "2 People", "$", R.raw.testvid4)
    )

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { venues.size })

    // Create one ExoPlayer per venue
    val exoPlayers = remember {
        venues.map { venue ->
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri("android.resource://${context.packageName}/${venue.videoResId}")
                setMediaItem(mediaItem)
                prepare()
            }
        }
    }

    // Release all players when leaving this screen
    DisposableEffect(Unit) {
        onDispose {
            exoPlayers.forEach { it.release() }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Wrap everything in a Box that intercepts pointer events
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Give it a high zIndex so it gets events before the pager can consume them
                .zIndex(10f)
                .pointerInput(Unit) {
                    // We want to continuously watch for "down -> up" cycles
                    // so we wrap everything in a while(true)
                    while (true) {
                        // Wait for a finger down
                        val down = awaitPointerEventScope { awaitFirstDown(requireUnconsumed = false) }
                        val downTime = System.currentTimeMillis()
                        var isLongPress = false

                        // Now wait until the user lifts or we detect a long press
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent(pass = PointerEventPass.Main)
                                // If user lifted finger
                                if (event.changes.any { it.changedToUp() }) {
                                    if (isLongPress) {
                                        // If we had paused, now resume
                                        exoPlayers[pagerState.currentPage].playWhenReady = true
                                    } else {
                                        // Short press => toggle volume
                                        val currentPlayer = exoPlayers[pagerState.currentPage]
                                        currentPlayer.volume =
                                            if (currentPlayer.volume > 0f) 0f else 1f
                                    }
                                    break
                                }
                                // If still holding, check for 1 second threshold
                                if (!isLongPress &&
                                    (System.currentTimeMillis() - downTime) >= 1000
                                ) {
                                    // Pause
                                    exoPlayers[pagerState.currentPage].playWhenReady = false
                                    isLongPress = true
                                }
                            }
                        }
                    }
                }
        ) {
            // Now we place our VerticalPager inside the same Box
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val isCurrentPage = (pagerState.currentPage == page)
                FullScreenMediaItem(
                    venue = venues[page],
                    exoPlayer = exoPlayers[page],
                    isActive = isCurrentPage
                )
            }

            // FAB
            FloatingActionButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-40).dp)
                    .zIndex(1f)
                    .size(64.dp),
                containerColor = Color(0xFF6200EE),
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Bottom Navigation Bar
            BottomNavigationBar(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

/**
 * A full-screen composable that displays a video (via [exoPlayer]) plus the text info.
 * This now does NOT have its own pointerInput. We just call [exoPlayer.playWhenReady = isActive].
 */
@Composable
fun FullScreenMediaItem(
    venue: Venue,
    exoPlayer: ExoPlayer,
    isActive: Boolean
) {
    LaunchedEffect(isActive) {
        // Automatically play/pause if this page is in view or not
        exoPlayer.playWhenReady = isActive
        if (isActive) {
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
        // Show the PlayerView
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
}

/**
 * A simple bottom bar with two icon buttons.
 */
@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(8.dp)
            .height(60.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationButton(iconResId = R.drawable.ic_search, label = "")
            Spacer(modifier = Modifier.width(72.dp))
            NavigationButton(iconResId = R.drawable.ic_profile, label = "")
        }
    }
}

@Composable
fun NavigationButton(iconResId: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = label,
                tint = Color.White
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}
