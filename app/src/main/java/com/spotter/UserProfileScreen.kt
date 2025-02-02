package com.spotter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.spotter.ui.BottomNavigationBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background
    ) {
        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabTitles = listOf("Posts", "Liked", "Saved Venues", "Events")

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Top Row: Back Button, Settings Button, and Get Verified
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.navigate("settingsScreen") }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings Button",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Button(
                            onClick = { /* Handle verification logic */ },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(text = "Get Verified", fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Profile Header
                UserProfileHeader()

                Spacer(modifier = Modifier.height(24.dp))

                // Tab Row
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 0.dp,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(text = title, fontSize = 16.sp) }
                        )
                    }
                }

                // Content Below Tabs
                Spacer(modifier = Modifier.height(16.dp))
                when (selectedTabIndex) {
                    0 -> TabContentPosts(navController)
                    1 -> TabContentLiked(navController)
                    2 -> TabContentSavedVenues(navController)
                    3 -> TabContentEvents(navController)
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            // Bottom Navigation Bar
            BottomNavigationBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                navController = navController
            )
        }
    }
}

@Composable
fun TabContentPosts(navController: NavController) {
    // Replace this with your actual posts content
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Posts will be displayed here.")
    }
}

@Composable
fun TabContentLiked(navController: NavController) {
    // Replace this with your actual liked content
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Liked items will be displayed here.")
    }
}

@Composable
fun TabContentSavedVenues(navController: NavController) {
    // Replace this with your actual saved venues content
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Saved Venues will be displayed here.")
    }
}

@Composable
fun TabContentEvents(navController: NavController) {
    // Replace this with your actual events content
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Upcoming Events will be displayed here.")
    }
}

@Composable
fun UserProfileHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter("https://placekitten.com/200/200"),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Username and Handle
        Text(
            text = "John Doe",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "@john_doe123",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

