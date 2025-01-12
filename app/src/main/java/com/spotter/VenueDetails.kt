package com.spotter.ui

import android.app.ActionBar
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.spotter.sampledata.Venue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueDetailsScreen(navController: NavController, venue: Venue) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            if (isVideoResource(context, venue.contentResIds.first())) {
                val mediaItem = MediaItem.fromUri("android.resource://${context.packageName}/${venue.contentResIds.first()}")
                addMediaItem(mediaItem)
                prepare()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Stacking content and bottom navigation
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())  // Content scrolls
                .padding(bottom = 90.dp)  // Padding to avoid overlapping with navbar
        ) {
            // Top bar with a back button
            TopAppBar(
                title = { Text(text = venue.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )

            // Media Section (Full-screen video or image)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val isVideo = isVideoResource(context, venue.contentResIds.first())
                if (isVideo) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                layoutParams = FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT
                                )
                                player = exoPlayer
                                useController = true  // Show video controls
                            }
                        }
                    )
                } else {
                    Image(
                        painter = painterResource(id = venue.contentResIds.first()),  // Default to first image or video thumbnail
                        contentDescription = venue.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Gray)
                    )
                }
            }

            // Main content
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = venue.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = venue.distance, fontSize = 16.sp, color = Color.Gray)
                    Text(text = venue.costIndicator, fontSize = 16.sp, color = Color.Green)
                }

                Text(
                    text = venue.description,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Amenities:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    venue.amenities.forEach { amenity ->
                        Text(
                            text = amenity,
                            modifier = Modifier
                                .background(Color.LightGray, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            fontSize = 14.sp
                        )
                    }
                }

                Text(
                    text = "User Reviews:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                if (venue.reviews.isNotEmpty()) {
                    venue.reviews.forEach { review ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = review.username, fontWeight = FontWeight.Bold)
                                Text(text = "\"${review.comment}\"", textAlign = TextAlign.Justify)
                                Text(text = "Rating: ${review.rating}★", color = Color.Gray)
                            }
                        }
                    }
                } else {
                    Text(text = "No reviews yet!", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { /* Open venue's website or external link */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Visit Website")
                    }
                    Button(
                        onClick = { /* Save to favorites */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(text = "Save to Favorites")
                    }
                }
            }
        }

        // Fixed bottom navigation bar
        BottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}


