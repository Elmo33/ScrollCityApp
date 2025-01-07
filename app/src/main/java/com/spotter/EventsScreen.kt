package com.spotter.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

// Sample data class for events
//data class Event(val name: String, val date: String, val location: String)

@Composable
fun EventsScreen(navController: NavController) {
    // Sample events data
    val sampleEvents = listOf(
        Event(
            name = "Spring Music Festival",
            imageUrl = "https://example.com/spring-music-festival.jpg",
            distance = "1.2 km from city centre",
            rating = 9.0,
            capacityRange = "100-200"
        ),
        Event(
            name = "Tech Innovators Conference",
            imageUrl = "https://example.com/tech-conference.jpg",
            distance = "0.8 km from city centre",
            rating = 8.7,
            capacityRange = "200-500"
        ),
        Event(
            name = "International Food Fair",
            imageUrl = "https://example.com/food-fair.jpg",
            distance = "2.5 km from city centre",
            rating = 9.4,
            capacityRange = "50-100"
        ),
        Event(
            name = "Modern Art Exhibition",
            imageUrl = "https://example.com/art-exhibition.jpg",
            distance = "1.5 km from city centre",
            rating = 9.2,
            capacityRange = "100-300"
        ),
        Event(
            name = "City Marathon",
            imageUrl = "https://example.com/city-marathon.jpg",
            distance = "3.0 km from city centre",
            rating = 8.9,
            capacityRange = "500-1000"
        ),
        Event(
            name = "Outdoor Movie Night",
            imageUrl = "https://example.com/movie-night.jpg",
            distance = "0.5 km from city centre",
            rating = 9.1,
            capacityRange = "100-150"
        ),
        Event(
            name = "Stand-up Comedy Show",
            imageUrl = "https://example.com/comedy-show.jpg",
            distance = "1.8 km from city centre",
            rating = 9.5,
            capacityRange = "50-80"
        ),
        Event(
            name = "Jazz Night",
            imageUrl = "https://example.com/jazz-night.jpg",
            distance = "1.0 km from city centre",
            rating = 9.3,
            capacityRange = "50-200"
        ),
        Event(
            name = "Science Fair",
            imageUrl = "https://example.com/science-fair.jpg",
            distance = "0.7 km from city centre",
            rating = 8.8,
            capacityRange = "200-400"
        ),
        Event(
            name = "Cultural Heritage Festival",
            imageUrl = "https://example.com/cultural-festival.jpg",
            distance = "2.0 km from city centre",
            rating = 9.6,
            capacityRange = "300-600"
        )
    )


    var searchQuery by remember { mutableStateOf("") }
    val filteredEvents = sampleEvents.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.distance.contains(searchQuery, ignoreCase = true)
    }


    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Search Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.Gray)
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Search events...",
                                            color = Color.Gray,
                                            fontSize = 16.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filter Icon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { /* Handle filter logic */ }) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = "Filter Events",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Scrollable List of Events
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (filteredEvents.isEmpty()) {
                            item {
                                Text(
                                    text = "No events found.",
                                    fontSize = 18.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        } else {
                            items(filteredEvents) { event ->
                                EventCard(event = event)
                            }
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun EventCard(event: Event) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
//            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp
    ) {
        Column {
            // Image Section
            Box {
                Image(
                    painter = rememberAsyncImagePainter(event.imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
                // Rating Section
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            color = Color(0xFF001b4f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        text = "${event.rating} ★",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Details Section
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${event.distance} from city centre",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // "Hot Deal" Label
                Box(
                    modifier = Modifier
                        .background(Color(0xFF001b4f), shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "#Hot deal",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Free cancellation | Catering available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Modern facilities\nCapacity: ${event.capacityRange}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* Navigate to details */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001b4f))
                ) {
                    Text(text = "View details", color = Color.White)
                }
            }
        }
    }
}

// Sample Data Class
data class Event(
    val name: String,
    val imageUrl: String,
    val distance: String,
    val rating: Double,
    val capacityRange: String
)
