package com.spotter.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.spotter.R
import com.spotter.getSampleActivities
import com.spotter.ui.theme.PrimaryColor
import com.spotter.ui.theme.SurfaceColor
import kotlinx.coroutines.launch
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.map

// Main Composable for Filter Activities Screen
@Composable
fun FilterActivitiesScreen(navController: NavController) {
    val context = LocalContext.current

    var isAllSelected by remember { mutableStateOf(false) }
    var activities by remember { mutableStateOf(getSampleActivities()) }

    // NEW: Obtain the FilterViewModel and collect its state
    val filterViewModel: FilterViewModel = viewModel()
    val filters by filterViewModel.filterStateFlow.collectAsState(
        initial = FilterStateData(
            peopleRange = 1f..5f,
            costRange = 0f..3f,
            timeRange = 0f..3f,
            distanceRange = 1f..50f
        )
    )

    // NEW: Create local mutable states for each slider to allow live updates
    var peopleRange by remember { mutableStateOf(filters.peopleRange) }
    var timeRange by remember { mutableStateOf(filters.timeRange) }
    var costRange by remember { mutableStateOf(filters.costRange) }
    var distanceRange by remember { mutableStateOf(filters.distanceRange) }

    // NEW: Whenever the ViewModel emits a new filters object, update our local states
    LaunchedEffect(filters) {
        peopleRange = filters.peopleRange
        timeRange = filters.timeRange
        costRange = filters.costRange
        distanceRange = filters.distanceRange
    }

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

                Spacer(modifier = Modifier.height(16.dp))

                RangeSliderWithDefaultThumbs(
                    valueRange = peopleRange,
                    onRangeChange = { peopleRange = it },
                    steps = 3,
                    labelPrefix = "Number of People:",
                    valueFormatter = { value -> value.toInt().toString() }
                )

                RangeSliderWithDefaultThumbs(
                    valueRange = timeRange,
                    onRangeChange = { timeRange = it },
                    steps = 2,
                    labelPrefix = "Preferred Time:",
                    valueFormatter = { value ->
                        listOf("Morning", "Afternoon", "Evening", "Night")[value.toInt()]
                    }
                )

                RangeSliderWithDefaultThumbs(
                    valueRange = costRange,
                    onRangeChange = { costRange = it },
                    steps = 2,
                    labelPrefix = "Cost:",
                    valueFormatter = { value -> listOf("Free", "$", "$$", "$$$+")[value.toInt()] },
                    singleThumb = true
                )

                RangeSliderWithDefaultThumbs(
                    valueRange = distanceRange,
                    onRangeChange = { distanceRange = it },
                    steps = 48,
                    labelPrefix = "Distance:",
                    valueFormatter = { value ->
                        if (value >= 50f) "50+ km" else "${value.toInt()} km"
                    },
                    singleThumb = true
                )


                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Include/Exclude Venues",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Switch(
                        checked = isAllSelected,
                        onCheckedChange = {
                            isAllSelected = it
                            activities = activities.map { activity ->
                                activity.copy(isSelected = it)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ActivitiesGrid(
                    activities = activities,
                    isAllSelected = isAllSelected,
                    onToggleAll = {
                        isAllSelected = !isAllSelected
                        activities = activities.map { it.copy(isSelected = isAllSelected) }
                    },
                    onToggleItem = { activity ->
                        activities = activities.map {
                            if (it.name == activity.name) it.copy(isSelected = !it.isSelected)
                            else it
                        }
                    }
                )

                Spacer(modifier = Modifier.height(60.dp))
            }

            Button(
                onClick = {
                    filterViewModel.saveFilters(
                        FilterStateData(
                            peopleRange = peopleRange,
                            costRange = costRange,
                            timeRange = timeRange,
                            distanceRange = distanceRange
                        )
                    )
                    navController.navigate("mainScreen")
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth(),
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
fun RangeSliderWithDefaultThumbs(
    valueRange: ClosedFloatingPointRange<Float>,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    steps: Int,
    labelPrefix: String,
    valueFormatter: (Float) -> String,
    singleThumb: Boolean = false
) {
    val density = LocalDensity.current
    var sliderRange by remember { mutableStateOf(valueRange) }
    var sliderValue by remember { mutableStateOf(valueRange.endInclusive) }
    var thumbRadiusPx by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        thumbRadiusPx = with(density) { 10.dp.toPx() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
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

        if (singleThumb) {
            Slider(
                value = sliderValue,
                onValueChange = { newValue ->
                    sliderValue = newValue
                    onRangeChange(valueRange.start..newValue)
                },
                valueRange = valueRange,
                steps = steps,
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            RangeSlider(
                value = sliderRange,
                onValueChange = {
                    sliderRange = it
                    onRangeChange(it)
                },
                valueRange = valueRange,
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = Color.LightGray,
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}




// Activities Grid Composable
@Composable
fun ActivitiesGrid(activities: List<ActivityItem>, isAllSelected: Boolean, onToggleAll: () -> Unit, onToggleItem: (ActivityItem) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val gridItemsPerRow = 5
        val rows = activities.chunked(gridItemsPerRow)
        val placeholders = listOf(
            "Bowling", "Billiards", "Table Tennis", "Go-Kart", "Movies",
            "Arcade", "Escape Room", "Mini Golf", "Laser Tag", "Paintball",
            "Trampoline", "Ice Skating", "Roller Skating", "Climbing", "VR Gaming",
            "Karaoke", "Live Music", "Museums", "Zoos", "Botanical Gardens",
            "Theme Parks", "Sports", "Nightlife", "Restaurants", "Fast Food"
        )

        var placeholderIndex = 0

        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEach { activity ->
                    val isSelected = activity.isSelected
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(if (isSelected) PrimaryColor else SurfaceColor, RoundedCornerShape(12.dp))
                            .clickable { onToggleItem(activity) },
                        contentAlignment = Alignment.Center
                    ) {
                        /* Commenting out the icon */
                        // Icon(
                        //     painter = painterResource(id = activity.iconResId),
                        //     contentDescription = activity.name,
                        //     tint = Color.White,
                        //     modifier = Modifier.size(32.dp)
                        // )

                        Text(
                            text = placeholders[placeholderIndex % placeholders.size],
                            color = Color.White,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(4.dp)
                        )

                        placeholderIndex++
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


// Sample Data Class and Function for Activities
data class ActivityItem(val name: String, val iconResId: Int, var isSelected: Boolean = false)

val RangeSaver: Saver<ClosedFloatingPointRange<Float>, List<Float>> = Saver(
    save = { listOf(it.start, it.endInclusive) },
    restore = { restoredList -> restoredList[0]..restoredList[1] }
)

@Preview(showBackground = true)
@Composable
fun FilterActivitiesScreenPreview() {
    FilterActivitiesScreen(navController = rememberNavController())
}



val Context.dataStore by preferencesDataStore(name = "filters_preferences")


// NEW: Create (or update) your FilterStateData data class if not already defined:
data class FilterStateData(
    val peopleRange: ClosedFloatingPointRange<Float>,
    val costRange: ClosedFloatingPointRange<Float>,
    val timeRange: ClosedFloatingPointRange<Float>,
    val distanceRange: ClosedFloatingPointRange<Float>
)

// NEW: Add the FilterViewModel class.
class FilterViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    val filterStateFlow = context.dataStore.data.map { preferences ->
        FilterStateData(
            peopleRange = (preferences[floatPreferencesKey("people_start")] ?: 1f)..(preferences[floatPreferencesKey("people_end")] ?: 5f),
            costRange = (preferences[floatPreferencesKey("cost_start")] ?: 0f)..(preferences[floatPreferencesKey("cost_end")] ?: 3f),
            timeRange = (preferences[floatPreferencesKey("time_start")] ?: 0f)..(preferences[floatPreferencesKey("time_end")] ?: 3f),
            distanceRange = (preferences[floatPreferencesKey("distance_start")] ?: 1f)..(preferences[floatPreferencesKey("distance_end")] ?: 50f)
        )
    }

    fun saveFilters(newFilters: FilterStateData) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[floatPreferencesKey("people_start")] = newFilters.peopleRange.start
                preferences[floatPreferencesKey("people_end")] = newFilters.peopleRange.endInclusive
                preferences[floatPreferencesKey("cost_start")] = newFilters.costRange.start
                preferences[floatPreferencesKey("cost_end")] = newFilters.costRange.endInclusive
                preferences[floatPreferencesKey("time_start")] = newFilters.timeRange.start
                preferences[floatPreferencesKey("time_end")] = newFilters.timeRange.endInclusive
                preferences[floatPreferencesKey("distance_start")] = newFilters.distanceRange.start
                preferences[floatPreferencesKey("distance_end")] = newFilters.distanceRange.endInclusive
            }
        }
    }
}