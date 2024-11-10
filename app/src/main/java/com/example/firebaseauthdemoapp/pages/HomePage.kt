package com.example.firebaseauthdemoapp.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.firebaseauthdemoapp.AuthState
import com.example.firebaseauthdemoapp.AuthViewModel
import com.example.firebaseauthdemoapp.HomeViewModel
import com.example.firebaseauthdemoapp.Item

// ... (previous imports remain the same)
import androidx.compose.ui.graphics.Color

object AppTheme {
    val Primary = Color(0xFFDA7756)      // Coral/salmon color
    val Background = Color(0xFFEDCDBF)    // Soft peachy background
    val OnPrimary = Color.White          // White text on primary color
    val TextGray = Color(0xFF666666)     // Gray for secondary text
    val CardBackground = Color.White     // White background for cards
    val Divider = Color(0xFFE5E5E5)     // Light gray for dividers
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel = viewModel()
) {
    val authState = authViewModel.authState.observeAsState()
    var searchQuery by remember { mutableStateOf("") }
    val items by homeViewModel.recentItems.collectAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    Scaffold(
        containerColor = AppTheme.Background,
        topBar = {
            SmallTopAppBar(
                title = { Text("LostLink", color = AppTheme.Primary) },
                actions = {
                    IconButton(onClick = { /* Open notifications */ }) {
                        Icon(
                            Icons.Filled.Notifications,
                            "Notifications",
                            tint = AppTheme.Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.White,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(AppTheme.Background)
        ) {
            TextButton(
                onClick = { authViewModel.signout() },
                modifier = Modifier.align(Alignment.End).padding(horizontal = 16.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AppTheme.Primary
                )
            ) {
                Text(text = "Sign out")
            }

            Spacer(modifier = Modifier.height(16.dp))

            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { /* Navigate or perform search action */ },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            QuickStats(
                foundCount = homeViewModel.foundItemsCount,
                returnedCount = homeViewModel.returnedItemsCount,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        "Recent Lost Items",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppTheme.Primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(items) { item ->
                    ItemCard(
                        item = item,
                        onClick = { /* Navigate to item details */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search lost items...", color = AppTheme.TextGray) },
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(Icons.Filled.Search, "Search", tint = AppTheme.Primary)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppTheme.Primary,
            unfocusedBorderColor = AppTheme.TextGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun QuickStats(
    foundCount: Int,
    returnedCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatCard(
            title = "Items Found",
            count = foundCount,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        StatCard(
            title = "Items Returned",
            count = returnedCount,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = AppTheme.Primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.TextGray
            )
        }
    }
}

@Composable
fun ItemCard(
    item: Item,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppTheme.Primary
                )
                Text(
                    text = item.category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.TextGray
                )
                Text(
                    text = "Lost on ${item.dateLost}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.TextGray
                )
            }
            if (item.matchPercentage != null) {
                Text(
                    text = "${(item.matchPercentage * 100).toInt()}% Match",
                    color = AppTheme.Primary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}