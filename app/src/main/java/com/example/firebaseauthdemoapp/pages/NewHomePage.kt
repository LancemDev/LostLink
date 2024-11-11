package com.example.firebaseauthdemoapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewHomePage(
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
            .background(Color(0xFFEDCDBF)), // Set background here
        topBar = {
            SmallTopAppBar(
                title = { Text("LostLink", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFFDA7756)) },
                actions = {
                    IconButton(onClick = { /* Open notifications */ }) {
                        Icon(Icons.Filled.Notifications, "Notifications", tint = Color(0xFFDA7756))
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFEDCDBF)) // Also set background here
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to the Home Page",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDA7756)
                )
                Text(
                    text = "Explore the app and enjoy the features.",
                    fontSize = 16.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )
                Button(
                    onClick = {
                        if (!isSigningOut) {
                            isSigningOut = true
                            authViewModel.signout()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDA7756)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(text = "Sign Out", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}
