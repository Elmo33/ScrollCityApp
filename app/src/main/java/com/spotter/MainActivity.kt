package com.spotter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.spotter.ui.MainScrollScreen
import androidx.compose.ui.Modifier
import com.spotter.ui.theme.SpotterTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Text(
                        text = "Profile Screen",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}