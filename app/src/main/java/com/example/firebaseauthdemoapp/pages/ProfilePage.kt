package com.example.firebaseauthdemoapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.firebaseauthdemoapp.AuthState
import com.example.firebaseauthdemoapp.AuthViewModel
import com.example.firebaseauthdemoapp.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.observeAsState()
    var isSigningOut by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated && isSigningOut) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
            isSigningOut = false
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.Background), // Use the theme's background color
        topBar = {
            SmallTopAppBar(
                title = {
                    Text("Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AppTheme.OnPrimary)
                },
                actions = {
                    IconButton(onClick = { /* Open notifications */ }) {
                        Icon(Icons.Filled.Notifications, "Notifications", tint = AppTheme.OnPrimary)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = AppTheme.Primary)
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppTheme.Background) // Also set background here
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Add profile image (use a placeholder image for now)
                // You can replace this with an Image if you have a profile picture
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(AppTheme.Primary, RoundedCornerShape(60.dp))
                ) {
                    // Image or Icon for profile picture
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.White
                    )
                }

                // Profile Name
                Text(
                    text = "John Doe", // Use actual user data here
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.OnSurface,
                    modifier = Modifier.padding(top = 16.dp)
                )

                // Email or additional information
                Text(
                    text = "john.doe@example.com", // Replace with actual email
                    fontSize = 16.sp,
                    color = AppTheme.TextGray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Sign Out Button
                Button(
                    onClick = {
                        if (!isSigningOut) {
                            isSigningOut = true
                            authViewModel.signout()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppTheme.Primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(text = "Sign Out", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}
