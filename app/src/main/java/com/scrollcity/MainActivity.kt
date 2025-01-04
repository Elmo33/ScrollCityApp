package com.scrollcity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.scrollcity.ui.MainScrollScreen
import com.scrollcity.ui.theme.ScrollCityTheme
import androidx.compose.runtime.Composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScrollCityApp()
        }
    }
}

@Composable
fun ScrollCityApp() {
    ScrollCityTheme {
        MainScrollScreen()
    }
}
