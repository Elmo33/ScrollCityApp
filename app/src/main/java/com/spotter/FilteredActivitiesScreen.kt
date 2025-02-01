package com.spotter.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.spotter.R
import com.spotter.getSampleActivities

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

    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Scrollable content
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(top = dimensionResource(R.dimen.top_padding))
                    .padding(horizontal = 8.dp)
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
                    .padding(top=40.dp)
                    .padding(horizontal = 32.dp)
                    .padding(bottom = dimensionResource(R.dimen.navbar_size))
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    text = "Select Your Desired Venues",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

// People Slider
                RangeSliderWithIcons(
                    valueRange = 1f..5f,
                    onRangeChange = { peopleRange = it },
                    steps = 3,
                    labelPrefix = "Number of People:",
                    valueFormatter = { value -> value.toInt().toString() },
                    iconResource = R.drawable.single // Custom person icon for thumbs
                )

// Cost Slider
                val costLabels = listOf("Free", "$", "$$", "$$$+")
                RangeSliderWithIcons(
                    valueRange = 0f..3f,
                    onRangeChange = { costRange = it },
                    steps = 2,
                    labelPrefix = "Cost:",
                    valueFormatter = { value -> costLabels[value.toInt()] },
                    iconResource = R.drawable.dollar
                )

// Time Slider
                RangeSliderWithIcons(
                    valueRange = 0f..3f,
                    onRangeChange = { timeRange = it },
                    steps = timePeriods.size - 2,
                    labelPrefix = "Preferred Time:",
                    valueFormatter = { value -> timePeriods[value.toInt()] }
                )



// Distance Slider (single-thumb version)
                RangeSliderWithIcons(
                    valueRange = 1f..50f,
                    onRangeChange = { distanceRange = it },
                    steps = 48,
                    labelPrefix = "Distance:",
                    valueFormatter = { value -> if (value >= 50f) "50+ km" else "${value.toInt()} km" },
                    singleThumb = true
                )




                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Include/Exclude Venues",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
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
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    steps: Int,
    labelPrefix: String,
    valueFormatter: (Float) -> String,
    iconResource: Int? = null,
    singleThumb: Boolean = false
) {
    val density = LocalDensity.current
    // For the two-thumb slider, default to the full range.
    // For the single-thumb slider, we only adjust the maximum.
    var sliderRange by remember { mutableStateOf(valueRange) }
    var sliderValue by remember { mutableStateOf(valueRange.endInclusive) }
    var sliderWidth by remember { mutableStateOf(0f) }
    var thumbRadiusPx by remember { mutableStateOf(0f) }
    // (This variable is here if you want to adjust the icon’s offset based on its size.)
    var iconSizePx by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        thumbRadiusPx = with(density) { 10.dp.toPx() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Use a different label depending on slider type.
        val labelText = if (singleThumb) {
            "$labelPrefix ${valueFormatter(sliderValue)}"
        } else {
            if (sliderRange.start == sliderRange.endInclusive) {
                "$labelPrefix ${valueFormatter(sliderRange.start)}"
            } else {
                "$labelPrefix ${valueFormatter(sliderRange.start)} - ${valueFormatter(sliderRange.endInclusive)}"
            }
        }
        Text(
            text = labelText,
            style = MaterialTheme.typography.bodyMedium
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // These will hold the X position(s) for the thumb(s)
            var startThumbX by remember { mutableStateOf(0f) }
            var endThumbX by remember { mutableStateOf(0f) }

            if (singleThumb) {
                // A single-thumb slider: we animate one icon offset for a fun “dangling” effect.
                val iconYOffset = remember { Animatable(0f) }
                LaunchedEffect(sliderValue) {
                    iconYOffset.animateTo(
                        targetValue = (-10..10).random().toFloat(),
                        animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f)
                    )
                }

                Slider(
                    value = sliderValue,
                    onValueChange = { newValue ->
                        sliderValue = newValue
                        // Construct a range from the minimum (fixed) to the new maximum.
                        onRangeChange(valueRange.start..newValue)
                    },
                    valueRange = valueRange,
                    steps = steps,
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            sliderWidth = coordinates.size.width.toFloat()
                            val trackWidth = sliderWidth - 2 * thumbRadiusPx
                            // Calculate the thumb’s X position.
                            startThumbX = thumbRadiusPx +
                                    ((sliderValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * trackWidth
                        }
                )
//
//                if (iconResource != null) {
//                    Icon(
//                        imageVector = ImageVector.vectorResource(id = iconResource),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(32.dp)
//                            .offset {
//                                IntOffset(
//                                    // Use the calculated thumb position; adjust the X/Y offsets as needed.
//                                    (startThumbX - iconSizePx / 2).toInt() - 55,
//                                    (iconYOffset.value).toInt() + 35
//                                )
//                            },
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
            } else {
                // Two-thumb slider.
                val startIconYOffset = remember { Animatable(0f) }
                val endIconYOffset = remember { Animatable(0f) }
                LaunchedEffect(sliderRange.start) {
                    startIconYOffset.animateTo(
                        targetValue = (-10..10).random().toFloat(),
                        animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f)
                    )
                }
                LaunchedEffect(sliderRange.endInclusive) {
                    endIconYOffset.animateTo(
                        targetValue = (-10..10).random().toFloat(),
                        animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f)
                    )
                }

                val interactionSource = remember { MutableInteractionSource() }

                RangeSlider(
                    value = sliderRange,
                    onValueChange = {
                        sliderRange = it
                        onRangeChange(it)
                    },
                    valueRange = valueRange,
                    steps = steps,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Transparent,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Color.LightGray,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .indication(interactionSource, null)
                        .onGloballyPositioned { coordinates ->
                            sliderWidth = coordinates.size.width.toFloat()
                            val trackWidth = sliderWidth - 2 * thumbRadiusPx
                            startThumbX = thumbRadiusPx +
                                    ((sliderRange.start - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * trackWidth
                            endThumbX = thumbRadiusPx +
                                    ((sliderRange.endInclusive - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * trackWidth
                        }
                )

                if (iconResource != null) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = iconResource),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .offset {
                                IntOffset(
                                    (startThumbX - iconSizePx / 2).toInt() - 55,
                                    (startIconYOffset.value).toInt() + 35
                                )
                            },
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(id = iconResource),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .offset {
                                IntOffset(
                                    (endThumbX - iconSizePx / 2).toInt() - 55,
                                    (endIconYOffset.value).toInt() + 35
                                )
                            },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
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


@Preview(showBackground = true)
@Composable
fun FilterActivitiesScreenPreview() {
    FilterActivitiesScreen(navController = rememberNavController())
}
