package com.spotter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.spotter.sampledata.Venue
import com.spotter.ui.BottomNavigationBar


@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VenueDetailsScreen(navController: NavController, venue: Venue) {
    val context = LocalContext.current


    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            // Add the media items for the venue
            venue.contentResIds.forEach { resId ->
                addMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/$resId"))
            }
            prepare()
        }
    }

    // Release the ExoPlayer when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    // Stacking content and bottom navigation
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(top = context.resources.getDimension(R.dimen.top_padding).dp)
                .zIndex(1f)// Custom size for icon button
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_ic),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.size(context.resources.getDimension(R.dimen.icon_buttons).dp) // Actual icon size
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())// To avoid overlapping with the navbar
        ) {
            // Media carousel (smaller size)
            Box(modifier = Modifier.height(300.dp)) {
                HorizontalMediaScroll(
                    contentResIds = venue.contentResIds,
                    exoPlayer = exoPlayer,
                    context = context,
                    isActive = true,
                    venue = venue,
                    modifier = Modifier.padding(top = 40.dp),
                    isDetailsPage = true
                )// Bounded height
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)) // Apply rounded corners
                    .background(MaterialTheme.colorScheme.surface) // Background color of the box
                    .padding(16.dp) // Add inner padding to the Column
            ) {
                Text(
                    text = venue.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                Text(
                    text = venue.address,
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 4.dp),
                ) {
                    Text(
                        text = venue.distance,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = venue.costIndicator,
                        fontSize = 16.sp,
                        color = Color.Green
                    )
                }
                Text(
                    text = venue.description,
                    style = MaterialTheme.typography.bodyMedium,

                    )
                Text(
                    text = venue.phoneNum,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp), // Spacing around the divider
                    thickness = 1.dp,
                    color = Color.DarkGray
                )

                // Reviews and Button Row
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Reviews in a Circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp) // Circle size
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary) // Circle background color
                    ) {
                        Text(
                            text = "9.68", // Display decimal number
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Leave a Review Button
                    Button(
                        onClick = { /* Add leave review action */ },
                        modifier = Modifier
                            .padding(start = 16.dp)
                    ) {
                        Text(text = "Leave a Review")
                    }

                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.right_arrow_ic),
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier.size(context.resources.getDimension(R.dimen.icon_buttons).dp) // Actual icon size
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

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

//                Text(
//                    text = "User Reviews:",
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(vertical = 8.dp)
//                )
//                if (venue.reviews.isNotEmpty()) {
//                    venue.reviews.forEach { review ->
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 4.dp),
//                            shape = RoundedCornerShape(8.dp),
//                        ) {
//                            Column(modifier = Modifier.padding(8.dp)) {
//                                Text(text = review.username, fontWeight = FontWeight.Bold)
//                                Text(text = "\"${review.comment}\"", textAlign = TextAlign.Justify)
//                                Text(text = "Rating: ${review.rating}★", color = Color.Gray)
//                            }
//                        }
//                    }
//                } else {
//                    Text(text = "No reviews yet!", color = Color.Gray)
//                }

//                Spacer(modifier = Modifier.height(16.dp))

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


