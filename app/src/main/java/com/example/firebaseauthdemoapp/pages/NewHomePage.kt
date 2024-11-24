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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.firebaseauthdemoapp.AuthViewModel
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.geometry.Offset
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale

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
    var isSearchBarVisible by remember { mutableStateOf(false) }

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

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Animated Background Gradient
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFF0EA),  // Light peachy
                        Color(0xFFFFE4D6),  // Soft peachy
                        Color(0xFFFFD4C2),  // Medium peachy
                        Color(0xFFFFF0EA)   // Back to light peachy
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
            )
        }

        // Main Content
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,  // Make scaffold background transparent
            topBar = {
                SmallTopAppBar(
                    title = { 
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.scale(scale)  // Add subtle pulse animation to logo
                        ) {
                            Text(
                                "LostLink",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp,
                                color = Color(0xFFDA7756),
                                fontFamily = FontFamily.Serif
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { /* Open notifications */ },
                            modifier = Modifier
                                .scale(0.9f)
                                .background(
                                    Color(0xFFDA7756).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(4.dp)
                        ) {
                            Icon(
                                Icons.Filled.Notifications,
                                "Notifications",
                                tint = Color(0xFFDA7756),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.White.copy(alpha = 0.8f)  // Semi-transparent
                    ),
                    modifier = Modifier
                        .shadow(8.dp)
                        .blur(2.dp)  // Add subtle blur effect
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Welcome Banner with enhanced design
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(
                            elevation = 12.dp,
                            spotColor = Color(0xFFDA7756).copy(alpha = 0.2f)
                        )
                        .scale(scale)  // Add subtle breathing animation
                ) {
                    AsyncImage(
                        model = "https://i.pinimg.com/736x/88/3d/99/883d997899a5a0ff019f40c5a170deb9.jpg",
                        contentDescription = "Welcome Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                "Welcome Back!",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Find your lost items",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }

                // Enhanced Search Bar
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically() + slideInVertically(),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shadow(12.dp)
                            .background(
                                Color.White.copy(alpha = 0.9f),
                                RoundedCornerShape(16.dp)
                            ),
                        placeholder = { Text("Search lost items...") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFFDA7756)
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color(0xFFDA7756)
                        ),
                        singleLine = true
                    )
                }

                // Categories Section with enhanced header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Categories",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "View All",
                        tint = Color(0xFFDA7756),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Enhanced Categories Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
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
}

@Composable
fun CategoryCard(
    category: Category,
    onCategoryClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(
                indication = null,  // Remove ripple
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isPressed = true
                onCategoryClick()
            }
            .scale(if (isPressed) 0.95f else 1f)  // Add press animation
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFFDA7756).copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = category.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}