package com.example.firebaseauthdemoapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.firebaseauthdemoapp.pages.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@Composable
fun MyNavigationPage(modifier: Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.observeAsState()

    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Report", Icons.Default.Send),
        NavItem("Search", Icons.Default.Search),
        NavItem("Profile", Icons.Default.AccountCircle),
        NavItem("AI", Icons.Default.Call)
    )

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    when (authState) {
        is AuthState.Loading -> {
            LoadingScreen() // This will use the updated loading screen
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
                containerColor = AppTheme.Background, // Set background color for the entire screen
                bottomBar = {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        NavigationBar(
                            containerColor = Color.White, // White background for nav bar
                            contentColor = AppTheme.Primary,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(8.dp, shape = MaterialTheme.shapes.medium)
                        ) {
                            navItemList.forEachIndexed { index, navItem ->
                                NavigationBarItem(
                                    selected = selectedIndex == index,
                                    onClick = {
                                        selectedIndex = index
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = navItem.icon,
                                            contentDescription = navItem.label,
                                            tint = if (selectedIndex == index)
                                                AppTheme.Primary
                                            else
                                                AppTheme.TextGray
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = navItem.label,
                                            color = if (selectedIndex == index)
                                                AppTheme.Primary
                                            else
                                                AppTheme.TextGray
                                        )
                                    },
                                    alwaysShowLabel = false,
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = AppTheme.Primary,
                                        unselectedIconColor = AppTheme.TextGray,
                                        selectedTextColor = AppTheme.Primary,
                                        unselectedTextColor = AppTheme.TextGray,
                                        indicatorColor = AppTheme.Primary.copy(alpha = 0.1f)
                                    )
                                )
                            }
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
            LoadingScreen() // This will use the updated loading screen
        }
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    navController: NavController,
    authViewModel: AuthViewModel,
    fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
) {
    when (selectedIndex) {
        0 -> NewHomePage(navController = navController, authViewModel = authViewModel)
        1 -> ReportPage(viewModel = AppViewModel(), fusedLocationClient = fusedLocationClient)
        2 -> SearchPage()
        3 -> ProfilePage(navController = navController, authViewModel = authViewModel)
        4 -> ChatBot()
    }
}


@Composable
fun LoadingScreen() {
    // Use AppTheme.Primary for the color of the CircularProgressIndicator
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = AppTheme.Primary // Updated to use the custom theme color
        )
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.Primary, // Set text color to your primary color
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = { /* Handle retry action */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.Primary,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retry")
            }
        }
    }
}

