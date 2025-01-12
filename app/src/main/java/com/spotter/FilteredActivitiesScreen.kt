package com.spotter.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.spotter.R

// Main Composable for Filter Activities Screen
@Composable
fun FilterActivitiesScreen(navController: NavController) {
    var peopleRange by remember { mutableStateOf(1f..5f) }
    var isPeopleEnabled by remember { mutableStateOf(false) }

    // Default: Cost range from "Free" to "$$$+"
    var costRange by remember { mutableStateOf(0f..3f) }
    var isCostEnabled by remember { mutableStateOf(false) }

    // Default: Time range from "Morning" to "Night" (full time range)
    var timeRange by remember { mutableStateOf(0f..3f) }
    var isTimeEnabled by remember { mutableStateOf(false) }

    // Default: Distance range from 1 km to 10 km (custom default for distance)
    var distanceRange by remember { mutableStateOf(1f..10f) }
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
                RangeSliderWithIcons(
                    valueRange = 1f..5f,
                    currentRange = peopleRange,
                    onRangeChange = { peopleRange = it },
                    steps = 3,
                    isEnabled = isPeopleEnabled,
                    onToggle = { isPeopleEnabled = !isPeopleEnabled },
                    labelPrefix = "Number of People:",
                    valueFormatter = { value -> value.toInt().toString() },
                    iconResource = R.drawable.single // Custom person icon for thumbs
                )


                // Cost Slider
                val costLabels = listOf("Free", "$", "$$", "$$$")
                RangeSliderWithIcons(
                    valueRange = 0f..3f,
                    currentRange = costRange,
                    onRangeChange = { costRange = it },
                    steps = 2,
                    isEnabled = isCostEnabled,
                    onToggle = { isCostEnabled = !isCostEnabled },
                    labelPrefix = "Cost:",
                    valueFormatter = { value -> costLabels[value.toInt()] },
                    iconResource = R.drawable.dollar
                )


                // Time Slider
                RangeSliderWithIcons(
                    valueRange = 0f..3f,
                    currentRange = timeRange,
                    onRangeChange = { timeRange = it },
                    steps = timePeriods.size - 2,
                    isEnabled = isTimeEnabled,
                    onToggle = { isTimeEnabled = !isTimeEnabled },
                    labelPrefix = "Preferred Time:",
                    valueFormatter = { value -> timePeriods[value.toInt()] }
                )


                // Distance Slider
                RangeSliderWithIcons(
                    valueRange = 1f..50f,
                    currentRange = distanceRange,
                    onRangeChange = { distanceRange = it },
                    steps = 48,
                    isEnabled = isDistanceEnabled,
                    onToggle = { isDistanceEnabled = !isDistanceEnabled },
                    labelPrefix = "Distance:",
                    valueFormatter = { value -> if (value >= 50f) "50+ km" else "${value.toInt()} km" }
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
fun RangeSliderWithIcons(
    valueRange: ClosedFloatingPointRange<Float>,
    currentRange: ClosedFloatingPointRange<Float>,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    steps: Int,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    labelPrefix: String,
    valueFormatter: (Float) -> String,
    iconResource: Int? = null
) {
    val density = LocalDensity.current // Access the current screen density for unit conversion
    var internalEnabled by remember { mutableStateOf(isEnabled) }
    var sliderRange by remember { mutableStateOf(currentRange) }
    var sliderWidth by remember { mutableStateOf(0f) }
    var thumbRadiusPx by remember { mutableStateOf(0f) } // Store the thumb radius in pixels

    LaunchedEffect(Unit) {
        thumbRadiusPx = with(density) { 10.dp.toPx() } // Convert thumb radius from dp to px
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { internalEnabled = !internalEnabled; onToggle() }
    ) {
        Text(
            text = if (sliderRange.start == sliderRange.endInclusive) {
                "$labelPrefix ${valueFormatter(sliderRange.start)}"
            } else {
                "$labelPrefix ${valueFormatter(sliderRange.start)} - ${valueFormatter(sliderRange.endInclusive)}"
            },
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // Increased height to fit icons below
        ) {
            var startThumbX by remember { mutableStateOf(0f) }
            var endThumbX by remember { mutableStateOf(0f) }
            var iconSizePx by remember { mutableStateOf(0f) }

            RangeSlider(
                value = sliderRange,
                onValueChange = {
                    if (internalEnabled) {
                        sliderRange = it
                        onRangeChange(it)
                    }
                },
                valueRange = valueRange,
                steps = steps,
                enabled = internalEnabled,
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = if (internalEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
                    inactiveTrackColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        sliderWidth = coordinates.size.width.toFloat() // Full slider width
                        val trackWidth =
                            sliderWidth - 2 * thumbRadiusPx // Calculate the usable track width

                        // Compute thumb positions within the track
                        startThumbX =
                            thumbRadiusPx + ((sliderRange.start - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * trackWidth
                        endThumbX =
                            thumbRadiusPx + ((sliderRange.endInclusive - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * trackWidth
                    }
            )

            if (iconResource != null) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = iconResource),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .onGloballyPositioned { coordinates ->
                            iconSizePx = coordinates.size.width.toFloat() // Icon width in pixels
                        }
                        .offset { IntOffset((startThumbX - iconSizePx / 2).toInt(), 40) },
                    tint = if (internalEnabled) MaterialTheme.colorScheme.primary else Color.Gray
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = iconResource),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .offset { IntOffset((endThumbX - iconSizePx / 2).toInt(), 40) },
                    tint = if (internalEnabled) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
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
