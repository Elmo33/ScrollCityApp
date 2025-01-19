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
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.media3.common.MediaItem
import com.spotter.sampledata.Venue

@Composable
fun HorizontalMediaScroll(
    contentResIds: List<Int>, // List of resource IDs
    exoPlayer: ExoPlayer, // ExoPlayer instance
    context: Context, // Context for checking video resources
    modifier: Modifier = Modifier, // Modifier for the layout
    isDetailsPage: Boolean = false,
    indicatorColor: Color = Color.White, // Active indicator color
    inactiveIndicatorColor: Color = Color.Gray, // Inactive indicator color
    spacing: Dp = 12.dp, // Spacing between indicators
    indicatorWidth: Dp = 24.dp, // Width of each indicator
    indicatorHeight: Dp = 4.dp, // Height of each indicator
    isActive: Boolean,
    venue: Venue
) {
    val horizontalPagerState =rememberPagerState(initialPage = 0, pageCount = { venue.contentResIds.size })

    LaunchedEffect(isActive, horizontalPagerState.currentPage) {
        exoPlayer.playWhenReady = isActive && isVideoResource(
            context,
            contentResIds[horizontalPagerState.currentPage]
        )
        if (isActive && isVideoResource(
                context,
                contentResIds[horizontalPagerState.currentPage]
            )
        ) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }
    HorizontalPager(
        state = horizontalPagerState,
        modifier = Modifier.fillMaxWidth()
    ) { mediaIndex ->
        val mediaResId = contentResIds[mediaIndex]
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .aspectRatio(if (isDetailsPage) 16 / 7f else 16 / 9f) // Adjust aspect ratio for details page
                .background(Color.Black) // Prevent transparency
        ) {
            if (isVideoResource(context, mediaResId)) {
                // Video Player View
                AndroidView(
                    modifier = Modifier.fillMaxWidth(),
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
    }
    Box(modifier = modifier.fillMaxWidth()) {
        // Page Indicator Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // Align the indicators to the bottom
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


