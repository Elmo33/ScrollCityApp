package com.spotter

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import android.view.ViewGroup.LayoutParams
import androidx.compose.foundation.clickable
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.Player
import com.spotter.sampledata.Venue

@Composable
fun HorizontalScroller(
    state: PagerState,
    contentResIds: List<Int>,
    exoPlayer: ExoPlayer,
    context: Context,
    controller: Boolean = true,
    // Optional click callback (if provided, the item becomes clickable)
    onItemClick: ((Int) -> Unit)? = null
) {
    // Ensure the video repeats indefinitely
    LaunchedEffect(key1 = exoPlayer) {
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
    }

    HorizontalPager(
        state = state,
        modifier = Modifier.fillMaxSize() // or .fillMaxWidth() as needed
    ) { index ->
        val mediaResId = contentResIds[index]
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .then(
                    if (onItemClick != null) {
                        Modifier.clickable { onItemClick(index) }
                    } else Modifier
                )
        ) {
            if (isVideoResource(context, mediaResId)) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )
                            setBackgroundColor(android.graphics.Color.BLACK)
                            player = exoPlayer
                            useController = controller
                        }
                    }
                )
            } else {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = mediaResId),
                    contentDescription = "Image content",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}



@Composable
fun FullScreenMediaViewer(
    contentResIds: List<Int>,
    exoPlayer: ExoPlayer,
    context: Context,
    initialIndex: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    indicatorColor: Color = Color.White,
    inactiveIndicatorColor: Color = Color.Gray,
    spacing: Dp = 12.dp,
    indicatorWidth: Dp = 24.dp,
    indicatorHeight: Dp = 4.dp,
) {
    val horizontalPagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { contentResIds.size }
    )

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            val fullScreenPagerState = rememberPagerState(
                initialPage = initialIndex,
                pageCount = { contentResIds.size }
            )

            HorizontalScroller(fullScreenPagerState, contentResIds, exoPlayer, context)

            // Close Button
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close full-screen",
                    tint = Color.White
                )
            }
            PagerRow(
                contentResIds,
                modifier,
                horizontalPagerState,
                indicatorColor,
                inactiveIndicatorColor,
                spacing,
                indicatorWidth,
                indicatorHeight
            )
        }
    }
}

@Composable
fun PagerRow(
    contentResIds: List<Int>,
    modifier: Modifier = Modifier,
    horizontalPagerState: PagerState,
    indicatorColor: Color = Color.White,
    inactiveIndicatorColor: Color = Color.Gray,
    spacing: Dp,
    indicatorWidth: Dp,
    indicatorHeight: Dp
) {
    // Page Indicator Row
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(contentResIds.size) { index ->
                Box(
                    modifier = Modifier
                        .width(indicatorWidth)
                        .height(indicatorHeight)
                        .background(
                            color = if (horizontalPagerState.currentPage == index) indicatorColor else inactiveIndicatorColor,
                            shape = RoundedCornerShape(2.dp)
                        )
                        .zIndex(1f)
                )
                if (index != contentResIds.size - 1) {
                    Spacer(modifier = Modifier.width(spacing))
                }
            }
        }
    }
}

@Composable
fun HorizontalMediaScroll(
    contentResIds: List<Int>,
    exoPlayer: ExoPlayer,
    context: Context,
    modifier: Modifier = Modifier,
    isDetailsPage: Boolean = false,
    indicatorColor: Color = Color.White,
    inactiveIndicatorColor: Color = Color.Gray,
    spacing: Dp = 12.dp,
    indicatorWidth: Dp = 24.dp,
    indicatorHeight: Dp = 4.dp,
    isActive: Boolean,
    venue: Venue
) {
    // Create a pager state
    val horizontalPagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { contentResIds.size }
    )

    var isFullScreen by remember { mutableStateOf(false) }
    var selectedMediaIndex by remember { mutableStateOf(0) }

    // Control video playback based on the current page and active state.
    LaunchedEffect(isActive, horizontalPagerState.currentPage) {
        val currentResId = contentResIds[horizontalPagerState.currentPage]
        val isVideo = isVideoResource(context, currentResId)
        exoPlayer.playWhenReady = isActive && isVideo
        if (isActive && isVideo) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    // Use the modular HorizontalScroller instead of directly using HorizontalPager.
    HorizontalScroller(
        state = horizontalPagerState,
        contentResIds = contentResIds,
        exoPlayer = exoPlayer,
        context = context,
        // We disable the controller here to match your original usage.
        controller = false,
        // If this is a details page, make the media clickable to open the full-screen view.
        onItemClick = if (isDetailsPage) { index ->
            selectedMediaIndex = index
            isFullScreen = true
        } else null
    )

    // Render the pager indicators
    PagerRow(
        contentResIds = contentResIds,
        modifier = modifier,
        horizontalPagerState = horizontalPagerState,
        indicatorColor = indicatorColor,
        inactiveIndicatorColor = inactiveIndicatorColor,
        spacing = spacing,
        indicatorWidth = indicatorWidth,
        indicatorHeight = indicatorHeight
    )

    // Show the full-screen media viewer if needed.
    if (isFullScreen) {
        FullScreenMediaViewer(
            contentResIds = contentResIds,
            exoPlayer = exoPlayer,
            context = context,
            initialIndex = selectedMediaIndex,
            onClose = { isFullScreen = false },
            modifier = modifier,
            indicatorColor = indicatorColor,
            inactiveIndicatorColor = inactiveIndicatorColor,
            spacing = spacing,
            indicatorWidth = indicatorWidth,
            indicatorHeight = indicatorHeight,
        )
    }
}


