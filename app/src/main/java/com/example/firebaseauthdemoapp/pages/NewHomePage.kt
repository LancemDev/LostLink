package com.example.firebaseauthdemoapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.firebaseauthdemoapp.AuthState
import com.example.firebaseauthdemoapp.AuthViewModel

data class Category(
    val name: String,
    val imageUrl: String,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewHomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.observeAsState()
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf(
        Category("Electronics", "https://i.pinimg.com/736x/9f/a4/a6/9fa4a69d41ce831151c8bb5f2039bf1a.jpg", "electronics"),
        Category("Clothing", "/api/placeholder/150/150", "clothing"),
        Category("Backpacks", "/api/placeholder/150/150", "backpacks"),
        Category("Personal Accessories", "/api/placeholder/150/150", "accessories"),
        Category("School Supplies", "/api/placeholder/150/150", "school-supplies"),
        Category("ID & Cards", "/api/placeholder/150/150", "identification"),
        Category("Sports Gear", "/api/placeholder/150/150", "sports"),
        Category("Miscellaneous", "/api/placeholder/150/150", "miscellaneous")
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDCDBF)),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFEDCDBF))
                .padding(16.dp)
        ) {
            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                placeholder = { Text("Search lost items...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // Categories Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onCategoryClick = { navController.navigate(category.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onCategoryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onCategoryClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF7B3F00))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}