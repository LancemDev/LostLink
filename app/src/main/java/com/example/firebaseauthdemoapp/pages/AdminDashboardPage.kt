package com.example.firebaseauthdemoapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log
import androidx.compose.animation.core.*
import coil.compose.AsyncImage



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardPage(
    modifier: Modifier = Modifier,
    onItemClick: (Item) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var foundItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var claimed_items by remember { mutableStateOf<List<Item>>(emptyList()) }
    var filteredItems by remember { mutableStateOf<List<Item>?>(null) }

    // Stats state
    var totalItems by remember { mutableStateOf(0) }
    var pendingItems by remember { mutableStateOf(0) }
    var claimedItems by remember { mutableStateOf(0) }

    // Animation for welcome banner
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Fetch items from Firebase and update stats
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("found_items")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("AdminDashboard", "Listen failed", e)
                    return@addSnapshotListener
                }

                foundItems = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Item(
                            id = doc.id,
                            itemName = doc.getString("itemName") ?: "",
                            category = doc.getString("category") ?: "OTHER",
                            description = doc.getString("description") ?: "",
                            locationDescription = doc.getString("locationDescription") ?: "",
                            selectedDate = doc.getString("selectedDate") ?: "",
                            selectedTime = doc.getString("selectedTime") ?: "",
                            status = doc.getString("status") ?: "pending",
                            imageData = doc.getString("imageData")
                        )
                    } catch (e: Exception) {
                        Log.e("AdminDashboard", "Error parsing document", e)
                        null
                    }
                } ?: emptyList()




                // Update stats
                totalItems = foundItems.size
                pendingItems = foundItems.count { it.status == "pending" }

            }
    }
    LaunchedEffect(Unit) {
        // Fetch claimed items from the claimed_items collection
        Firebase.firestore.collection("claimed_items")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("AdminDashboard", "Listen failed", e)
                    return@addSnapshotListener
                }

                // Map the claimed items from the snapshot
                claimed_items = snapshot?.documents?.mapNotNull { document ->
                    try {
                        Item(
                            id = document.id,
                            itemName = document.getString("itemName") ?: "",
                            category = document.getString("category") ?: "OTHER",
                            description = document.getString("description") ?: "",
                            locationDescription = document.getString("locationDescription") ?: "",
                            selectedDate = document.getString("selectedDate") ?: "",
                            selectedTime = document.getString("selectedTime") ?: "",
                            status = document.getString("status") ?: "claimed",
                            imageData = document.getString("imageData")
                        )
                    } catch (e: Exception) {
                        Log.e("AdminDashboard", "Error parsing document", e)
                        null
                    }
                } ?: emptyList()

                // Update stats
//                totalItems = claimed_items.size
                claimedItems = claimed_items.size // Count all claimed items
            }
    }

    // Update filtered items when search query changes
    LaunchedEffect(searchQuery) {
        filteredItems = when {
            searchQuery.isEmpty() -> null
            else -> foundItems.filter {
                it.itemName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Welcome Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
                .shadow(
                    elevation = 12.dp,
                    spotColor = Color(0xFFDA7756).copy(alpha = 0.2f)
                )
                .scale(scale)
        ) {
            AsyncImage(
                model = "https://i.pinimg.com/736x/31/59/ad/3159ad868444116f950b8058abe79c98.jpg",
                contentDescription = "Admin Dashboard Banner",
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
                        "Welcome, Admin.",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Manage and track lost items",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
            placeholder = { Text("Search by item name...", fontSize = 18.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppTheme.Primary,
                unfocusedBorderColor = AppTheme.TextGray,
                containerColor = Color.White
            )
        )

        // Statistics Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticCard(
                title = "Total Items",
                value = totalItems,
                color = AppTheme.Primary
            )
            StatisticCard(
                title = "Pending",
                value = pendingItems,
                color = Color(0xFFFFA000)
            )
            StatisticCard(
                title = "Claimed",
                value = claimedItems,
                color = Color(0xFF4CAF50)
            )
        }

        when {
            filteredItems == null -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Enter a search term to view items",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppTheme.TextGray,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            }
            filteredItems?.isEmpty() == true -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matches found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppTheme.TextGray,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            }
            else -> {
                filteredItems?.let { items ->
                    Text(
                        text = "${items.size} items found",
                        modifier = Modifier.padding(bottom = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.TextGray
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items) { item ->
                            ItemCard(
                                item = item,
                                searchQuery = searchQuery,
                                onClick = { onItemClick(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemCard(
    item: Item,
    searchQuery: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (searchQuery.isNotEmpty() &&
                item.itemName.contains(searchQuery, ignoreCase = true)
            ) {
                AppTheme.Primary.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = buildAnnotatedString {
                        if (searchQuery.isEmpty()) {
                            append(item.itemName)
                        } else {
                            val startIndex = item.itemName.indexOf(searchQuery, ignoreCase = true)
                            if (startIndex >= 0) {
                                append(item.itemName.substring(0, startIndex))
                                withStyle(SpanStyle(
                                    background = AppTheme.Primary.copy(alpha = 0.3f),
                                    fontWeight = FontWeight.Bold
                                )) {
                                    append(item.itemName.substring(startIndex, startIndex + searchQuery.length))
                                }
                                append(item.itemName.substring(startIndex + searchQuery.length))
                            } else {
                                append(item.itemName)
                            }
                        }
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Category: ${item.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Location: ${item.locationDescription}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                StatusChip(status = item.status)
            }

            if (item.imageData != null) {
                AsyncImage(
                    model = item.imageData,
                    contentDescription = "Item image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "pending" -> Color(0xFFFFA000) to Color.White
        "claimed" -> Color(0xFF4CAF50) to Color.White
        "found" -> Color(0xFF2196F3) to Color.White
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = Modifier
            .padding(top = 4.dp),
        color = backgroundColor.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = status.capitalize(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = backgroundColor
        )
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: Int,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}