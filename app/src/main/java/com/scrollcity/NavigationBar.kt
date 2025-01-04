package com.scrollcity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* // For Box, Row, Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
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
import com.scrollcity.R

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController // Pass NavController to handle navigation
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(8.dp)
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp), // To create space for FAB
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationButton(
                iconResId = R.drawable.ic_search,
                label = "Activities",
                onClick = { navController.navigate("filterActivities") }
            )
            Spacer(modifier = Modifier.width(48.dp)) // Space for FAB
            NavigationButton(
                iconResId = R.drawable.ic_profile,
                label = "Profile",
                onClick = { navController.navigate("profileScreen") }
            )
        }

        // Centered FAB to navigate to MainScreen
        FloatingActionButton(
            onClick = { navController.navigate("mainScreen") }, // Navigate to MainScrollScreen
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp)
                .size(64.dp),
            containerColor = Color(0xFF6200EE),
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search),
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun NavigationButton(
    iconResId: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { onClick() }) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = label,
                tint = Color.White
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}
