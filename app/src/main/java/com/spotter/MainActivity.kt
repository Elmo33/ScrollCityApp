package com.spotter

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.spotter.ui.theme.SpotterTheme
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spotter.sampledata.provideSampleVenues
import com.spotter.ui.EventsScreen
import com.spotter.ui.FilterActivitiesScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Spotter()
        }
    }
}



@Composable
fun Spotter() {

    val navController = rememberNavController() // Create NavController instance
    SpotterTheme {
        NavHost(navController = navController, startDestination = "mainScreen") {
            // Main scroll screen
            composable("mainScreen") {
                MainScrollScreen(navController)
            }
            composable("eventsScreen"){
                EventsScreen(navController)
            }
            // Filter activities screen
            composable("filterActivities") {
                FilterActivitiesScreen(navController) // Pass NavController directly
            }
            // Profile screen placeholder
            composable("profileScreen") {
                UserProfileScreen(navController) // Call the UserProfileScreen composable
            }
            composable("venueDetails/{venueId}") { backStackEntry ->
                val venueId = backStackEntry.arguments?.getString("venueId")?.toInt() ?: 0
                val venue = provideSampleVenues().firstOrNull { it.id == venueId }
                    ?: throw IllegalArgumentException("Venue not found")
                VenueDetailsScreen(navController = navController, venue = venue)
            }
        }
    }
}