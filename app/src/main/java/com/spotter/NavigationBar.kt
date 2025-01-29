package com.spotter.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* // For Box, Row, Spacer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.spotter.R
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.zIndex

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)  // Fixed height for the bottom navigation bar
    ) {
        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .zIndex(0f)
        )

        // Navigation buttons (placed over the gradient)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)  // Align to the bottom
                .padding(bottom = 7.dp),  // Fixed padding
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationButton(
                iconResId = R.drawable.ic_event,
                label = "Events",
                onClick = { navController.navigate("eventsScreen") }
            )

            NavigationButton(
                iconResId = R.drawable.binoculars,
                label = "Discover",
                onClick = { navController.navigate("mainScreen") },
            )
            NavigationButton(
                iconResId = R.drawable.ic_profile,
                label = "Profile",
                onClick = { navController.navigate("profileScreen") }
            )
        }
    }
}

@Composable
fun NavigationButton(
    iconResId: Int,
    label: String,
    onClick: () -> Unit,
    iconSize: Int = 24,  // Default icon size (can be overridden)
    padding: Int = 0
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { onClick() },
            modifier = Modifier.size(iconSize.dp)
                .padding(bottom = padding.dp)// Custom size for icon button
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = label,
                tint = colorResource(id = R.color.white),
                modifier = Modifier.size(iconSize.dp)  // Actual icon size
            )
        }
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.White
        )
    }
}
