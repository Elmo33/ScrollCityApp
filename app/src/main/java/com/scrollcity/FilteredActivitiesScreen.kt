package com.scrollcity.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.scrollcity.R

// Main Composable for Filter Activities Screen
@Composable
fun FilterActivitiesScreen(navController: NavController) {
    // Slider states
    var peopleValue by remember { mutableStateOf(1f) }  // 1..10; if at 10, show "10+"
    var costValue by remember { mutableStateOf(0f) }    // 0..3; 0=empty, 1=$, 2=$$, 3=$$$+
    var timeValue by remember { mutableStateOf(12f) }   // 0..23 in 24-hour format
    var distanceValue by remember { mutableStateOf(1f) } // 1..50; if at 50, show "50+"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Text
                Text(
                    text = "Select Your Desired Venues",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ---- DRAGGABLE PROGRESS BARS START ----
                // People Slider
                Text(
                    text = "Number of People: ${if (peopleValue >= 10f) "10+" else peopleValue.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = peopleValue,
                    onValueChange = { peopleValue = it },
                    valueRange = 1f..10f,
                    steps = 8,  // Steps from 1..10 (there are 9 intervals, so steps=8)
                    modifier = Modifier.fillMaxWidth()
                )

                // Cost Slider
                // 0 = no cost symbol, 1=$, 2=$$, 3=$$$+
                Text(
                    text = "Cost: " + when (costValue.toInt()) {
                        0 -> "Free"
                        1 -> "$"
                        2 -> "$$"
                        else -> "$$$+"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = costValue,
                    onValueChange = { costValue = it },
                    valueRange = 0f..3f,
                    steps = 2, // 3 intervals means 2 steps
                    modifier = Modifier.fillMaxWidth()
                )

                // Time Slider (0–23 in 24-hour format)
                Text(
                    text = "Preferred Time: ${timeValue.toInt()}:00",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = timeValue,
                    onValueChange = { timeValue = it },
                    valueRange = 0f..23f,
                    steps = 22, // For 23 intervals from 0..23
                    modifier = Modifier.fillMaxWidth()
                )

                // Distance Slider
                Text(
                    text = "Distance: ${if (distanceValue >= 50f) "50+ km" else "${distanceValue.toInt()} km"}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = distanceValue,
                    onValueChange = { distanceValue = it },
                    valueRange = 1f..50f,
                    steps = 48,
                    modifier = Modifier.fillMaxWidth()
                )
                // ---- DRAGGABLE PROGRESS BARS END ----

                Spacer(modifier = Modifier.height(16.dp))

                // Include/Exclude Toggle Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Include/Exclude Venues",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Switch(checked = false, onCheckedChange = { /* TODO: Add logic */ })
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Activities Grid
                ActivitiesGrid(activities = getSampleActivities())

                Spacer(modifier = Modifier.weight(1f))
            }

            // Bottom Navigation Bar
            BottomNavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                navController = navController // Pass the NavController directly
            )
        }
    }
}

// Activities Grid Composable
@Composable
fun ActivitiesGrid(activities: List<ActivityItem>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val gridItemsPerRow = 5
        val rows = activities.chunked(gridItemsPerRow)

        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEach { activity ->
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.Black, RoundedCornerShape(12.dp))
                            .clickable { /* TODO: Handle activity selection */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = activity.iconResId),
                            contentDescription = activity.name,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Sample Data Class and Function for Activities
data class ActivityItem(val name: String, val iconResId: Int)

fun getSampleActivities(): List<ActivityItem> {
    return listOf(
        ActivityItem("Carting", R.drawable.ic_home),
        ActivityItem("Clubbing", R.drawable.ic_home),
        ActivityItem("Arcade", R.drawable.ic_home),
        ActivityItem("Go-Kart", R.drawable.ic_home),
        ActivityItem("Cafe", R.drawable.ic_home),
        ActivityItem("Music", R.drawable.ic_home),
        ActivityItem("Cinema", R.drawable.ic_home),
        ActivityItem("Bowling", R.drawable.ic_home),
        ActivityItem("Bar", R.drawable.ic_home),
        ActivityItem("Park", R.drawable.ic_home),
        ActivityItem("Gallery", R.drawable.ic_home),
        ActivityItem("Museum", R.drawable.ic_home),
        ActivityItem("Restaurant", R.drawable.ic_home),
        ActivityItem("Adventure", R.drawable.ic_home),
        ActivityItem("Beach", R.drawable.ic_home),
        ActivityItem("Hiking", R.drawable.ic_home),
        ActivityItem("Pool", R.drawable.ic_home),
        ActivityItem("Gym", R.drawable.ic_home),
        ActivityItem("Escape Room", R.drawable.ic_home),
        ActivityItem("Concert", R.drawable.ic_home),
        ActivityItem("Live Show", R.drawable.ic_home),
        ActivityItem("Paintball", R.drawable.ic_home),
        ActivityItem("Festival", R.drawable.ic_home),
        ActivityItem("Theater", R.drawable.ic_home),
        ActivityItem("Comedy Club", R.drawable.ic_home)
    )
}

@Preview(showBackground = true)
@Composable
fun FilterActivitiesScreenPreview() {
    FilterActivitiesScreen(navController = rememberNavController())
}
