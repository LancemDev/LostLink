package com.example.firebaseauthdemoapp

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.firebaseauthdemoapp.pages.*

@Composable
fun MyNavigationPage(modifier: Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.observeAsState()

    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Settings", Icons.Default.Settings),
        NavItem("Notifications", Icons.Default.Notifications),
    )

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    when (authState) {
        is AuthState.Loading -> {
            LoadingScreen()
        }

        is AuthState.Error -> {
            ErrorScreen((authState as AuthState.Error).message)
        }

        is AuthState.Unauthenticated -> {
            NavHost(
                navController = navController,
                startDestination = "login",
                builder = {
                    composable("login") {
                        LoginPage(modifier, navController, authViewModel)
                    }
                    composable("signup") {
                        SignupPage(modifier, navController, authViewModel)
                    }
                }
            )
        }

        is AuthState.Authenticated -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar {
                        navItemList.forEachIndexed { index, navItem ->
                            NavigationBarItem(
                                selected = selectedIndex == index,
                                onClick = {
                                    selectedIndex = index
                                },
                                icon = {
                                    Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                                },
                                label = {
                                    Text(text = navItem.label)
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                ContentScreen(
                    modifier = Modifier.padding(innerPadding),
                    selectedIndex = selectedIndex,
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }

        null -> {
            LoadingScreen()
        }
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex: Int, navController: NavController, authViewModel: AuthViewModel) {
    when(selectedIndex) {
        0 -> NewHomePage(navController = navController, authViewModel = authViewModel)
        1 -> ProfilePage()
        2 -> NotificationsPage()
    }
}

@Composable
fun LoadingScreen() {
    // Implement your loading screen UI
    Spacer(modifier = Modifier.height(16.dp))
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
fun ErrorScreen(message: String) {
    // Implement your error screen UI
}