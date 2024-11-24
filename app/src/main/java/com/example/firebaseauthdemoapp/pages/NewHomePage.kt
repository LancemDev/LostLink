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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
        Category("Clothing", "https://i.pinimg.com/736x/3d/ce/da/3dcedac1c78683821be902d1ab1bbf79.jpg", "clothing"),
        Category("Bags", "https://i.pinimg.com/736x/7d/28/1d/7d281d3722a76fb0b00dd485f6d2561d.jpg", "backpacks"),
        Category("Personal Accessories", "https://i.pinimg.com/736x/db/d6/39/dbd639e9e546c6374f53be87bf714991.jpg", "accessories"),
        Category("School Supplies", "https://i.pinimg.com/736x/9c/c8/58/9cc8584067434d74acd9630a7602a67f.jpg", "school-supplies"),
        Category("ID & Cards", "https://i.pinimg.com/736x/2c/b1/1f/2cb11f99587d98e80cedfeb5cb7dc02d.jpg", "identification"),
        Category("Sports & Gym Gear", "https://i.pinimg.com/736x/31/e2/51/31e251fd703f7af81e8028abeabbd69e.jpg", "sports"),
        Category("Miscellaneous", "https://i.pinimg.com/736x/50/7f/f5/507ff50090658bf85c2351bf4e5856de.jpg", "miscellaneous")
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
        ) {
            // Welcome Banner (reduced height from 200.dp to 160.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                // Banner Image
                AsyncImage(
                    model = "https://i.pinimg.com/736x/88/3d/99/883d997899a5a0ff019f40c5a170deb9.jpg",
                    contentDescription = "Welcome Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Overlay with Welcome Text
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Welcome to Home",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Find your lost items",
                            fontSize = 18.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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

            // Categories Grid with bottom padding
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp) // Added bottom padding
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
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Text Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}