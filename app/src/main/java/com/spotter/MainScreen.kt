package com.spotter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.FlowRowScopeInstance.align
//import androidx.compose.foundation.layout.FlowRowScopeInstance.align
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.spotter.sampledata.Venue
import com.spotter.sampledata.provideSampleVenues
import com.spotter.ui.BottomNavigationBar


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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = dimensionResource(R.dimen.navbar_size))
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
                        navController = navController
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


@Composable
fun FullScreenMediaItem(
    venue: Venue,
    exoPlayer: ExoPlayer,
    isActive: Boolean,
    navController: NavController
) {

    val context = LocalContext.current


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        IconButton(
            onClick = {
                navController.navigate(
                    "filterActivities"
                )
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(1f)
                .padding(top = dimensionResource(R.dimen.top_padding))
                .padding(horizontal = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_tune),
                contentDescription = "Open Filters",
                modifier = Modifier.size(dimensionResource(R.dimen.icon_buttons)),
                tint = Color.White,
            )
        }

        HorizontalMediaScroll(
            contentResIds = venue.contentResIds,
            exoPlayer = exoPlayer,
            context = context,
            isActive = isActive,
            venue = venue,
            modifier = Modifier.padding(top = 40.dp)
        )

        // Venue details (name, distance, description)
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Bottom-right aligned content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                ) {
                    ActionIconButton(
                        icon = painterResource(id = R.drawable.ic_heart),
                        contentDescription = "Add to Favorites",
                        onClick = { /* Add to favorites */ }
                    )
                    ActionIconButton(
                        icon = painterResource(id = R.drawable.ic_share),
                        contentDescription = "Share",
                        onClick = { /* Share the venue */ }
                    )
                }

            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33000000)) // 80% transparent black background
                    .clickable {  // Makes the entire section clickable
                        navController.navigate("venueDetails/${venue.id}")
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = venue.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = venue.distance,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = venue.costIndicator,
                            fontSize = 16.sp,
                            color = Color.Green
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = venue.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ActionIconButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(40.dp),
            tint = Color.White
        )
    }
}



