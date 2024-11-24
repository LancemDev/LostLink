package com.example.firebaseauthdemoapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardPage(
    modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    var foundItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var filteredItems by remember { mutableStateOf<List<Item>?>(null) }

    // Fetch items from Firebase
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
            }
    }

    // Update filtered items when search query changes
    LaunchedEffect(searchQuery) {
        filteredItems = when {
            searchQuery.isEmpty() -> null // No search active
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
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Search by item name...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppTheme.Primary,
                unfocusedBorderColor = AppTheme.TextGray
            )
        )

        when {
            filteredItems == null -> {
                // Initial state or empty search
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
                // No matches found
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
                // Show results
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
                                onClick = {
                                    val section = when (item.status) {
                                        "pending" -> "reported"
                                        "found" -> "found"
                                        else -> "reported"
                                    }
                                  }
                            )
                        }
                    }
                }
            }
        }
    }
}

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
                AppTheme.OnPrimary
            }
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                color = AppTheme.OnPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Category: ${item.category}",
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.TextGray
            )

            Text(
                text = "Location: ${item.locationDescription}",
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.TextGray
            )

            Text(
                text = "Status: ${item.status}",
                style = MaterialTheme.typography.bodyMedium,
                color = when (item.status) {
                    "pending" -> Color.Yellow
                    "matched" -> Color.Green
                    "found" -> Color.Blue
                    "closed" -> Color.Red
                    else -> AppTheme.TextGray
                }
            )
        }
    }
}