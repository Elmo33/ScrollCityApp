package com.spotter.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.spotter.R

// Main Composable for Filter Activities Screen
@Composable
fun FilterActivitiesScreen(navController: NavController) {
    var peopleValue by remember { mutableStateOf(1f) }
    var isPeopleEnabled by remember { mutableStateOf(false) }
    var costValue by remember { mutableStateOf(0f) }
    var isCostEnabled by remember { mutableStateOf(false) }
    var timeValue by remember { mutableStateOf(0f) }
    var isTimeEnabled by remember { mutableStateOf(false) }
    var distanceValue by remember { mutableStateOf(1f) }
    var isDistanceEnabled by remember { mutableStateOf(false) }
    val timePeriods = listOf("Morning", "Afternoon", "Evening", "Night")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = 90.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Select Your Desired Venues",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // People Slider
                Text(
                    text = "Number of People: ${if (peopleValue >= 5f) "5+" else peopleValue.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                SliderWithToggle(
                    value = peopleValue,
                    onValueChange = { peopleValue = it },
                    valueRange = 1f..5f,
                    steps = 3,
                    isEnabled = isPeopleEnabled,
                    onToggle = { isPeopleEnabled = !isPeopleEnabled }
                )

                // Cost Slider
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
                SliderWithToggle(
                    value = costValue,
                    onValueChange = { costValue = it },
                    valueRange = 0f..3f,
                    steps = 2,
                    isEnabled = isCostEnabled,
                    onToggle = { isCostEnabled = !isCostEnabled }
                )

                // Time Slider
                Text(
                    text = "Preferred Time: ${timePeriods[timeValue.toInt()]}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                SliderWithToggle(
                    value = timeValue,
                    onValueChange = { timeValue = it },
                    valueRange = 0f..3f,
                    steps = 3,
                    isEnabled = isTimeEnabled,
                    onToggle = { isTimeEnabled = !isTimeEnabled }
                )

                // Distance Slider
                Text(
                    text = "Distance: ${if (distanceValue >= 50f) "50+ km" else "${distanceValue.toInt()} km"}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                SliderWithToggle(
                    value = distanceValue,
                    onValueChange = { distanceValue = it },
                    valueRange = 1f..50f,
                    steps = 48,
                    isEnabled = isDistanceEnabled,
                    onToggle = { isDistanceEnabled = !isDistanceEnabled }
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                    Switch(checked = false, onCheckedChange = { /* TODO */ })
                }

                Spacer(modifier = Modifier.height(16.dp))

                ActivitiesGrid(activities = getSampleActivities())

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Fixed "Apply" Button
            Button(
                onClick = {
                    navController.navigate("mainScreen") // Navigate to Main Screen
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
                    .fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Apply",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            BottomNavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                navController = navController
            )
        }


    }
}

@Composable
fun SliderWithToggle(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Slider(
            value = value,
            onValueChange = { if (isEnabled) onValueChange(it) },
            valueRange = valueRange,
            steps = steps,
            enabled = isEnabled,
            colors = SliderDefaults.colors(
                thumbColor = if (isEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
                activeTrackColor = if (isEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
                inactiveTrackColor = Color.LightGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() } // Toggles enable/disable on click
        )
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
