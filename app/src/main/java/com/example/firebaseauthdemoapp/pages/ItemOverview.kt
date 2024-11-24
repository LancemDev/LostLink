package com.example.firebaseauthdemoapp.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.firebaseauthdemoapp.AppTheme
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.firebaseauthdemoapp.ImageUtils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image

data class Item(
    val id: String = "",
    val itemName: String = "",
    val category: String = "",
    val description: String = "",
    val locationDescription: String = "",
    val selectedDate: String = "",
    val selectedTime: String = "",
    val status: String = "",
    val imageData: String? = null
)

class ItemOverview : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ItemOverviewPage()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemOverviewPage() {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = AppTheme.Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Item Overview", color = AppTheme.OnPrimary) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = AppTheme.Primary
                    )
                },
                containerColor = AppTheme.Surface
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Reported Items") },
                    selectedContentColor = AppTheme.Primary,
                    unselectedContentColor = AppTheme.OnSurface.copy(alpha = 0.6f)
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Found Items") },
                    selectedContentColor = AppTheme.Primary,
                    unselectedContentColor = AppTheme.OnSurface.copy(alpha = 0.6f)
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Claimed Items") },
                    selectedContentColor = AppTheme.Primary,
                    unselectedContentColor = AppTheme.OnSurface.copy(alpha = 0.6f)
                )
            }

            when (selectedTabIndex) {
                0 -> ReportedItemsSection()
                1 -> FoundItemsSection()
                2 -> ClaimedItemsSection()
            }
        }
    }
}

@Composable
fun ReportedItemsSection() {
    val items = listOf(
        Item("Item 1", "Description of Item 1", "Reported"),
        Item("Item 2", "Description of Item 2", "Reported")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Reported Items", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        items.forEach { item ->
            ItemCard(item = item)
        }
    }
}

@Composable
fun FoundItemsSection() {
    var foundItems by remember { mutableStateOf<List<Item>>(emptyList()) }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("found_items")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FoundItemsSection", "Listen failed", e)
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
                        Log.e("FoundItemsSection", "Error parsing document", e)
                        null
                    }
                } ?: emptyList()
            }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(foundItems) { item ->
            ItemCard(item = item)
        }
    }
}

@Composable
fun ClaimedItemsSection() {
    val items = listOf(
        Item("Item 5", "Description of Item 5", "Claimed"),
        Item("Item 6", "Description of Item 6", "Claimed")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Claimed Items", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        items.forEach { item ->
            ItemCard(item = item)
        }
    }
}

@Composable
fun ItemCard(item: Item) {
    var expanded by remember { mutableStateOf(false) }
    
    // Convert category string to proper format
    val displayCategory = remember(item.category) {
        when (item.category.uppercase()) {
            "ELECTRONICS" -> "Electronics"
            "CLOTHING" -> "Clothing"
            "ACCESSORIES" -> "Accessories"
            "DOCUMENTS" -> "Documents"
            "KEYS" -> "Keys"
            "WALLET" -> "Wallet"
            "BAG" -> "Bag"
            "OTHER" -> "Other"
            else -> item.category // fallback to original value
        }
    }

    val bitmap = remember(item.imageData) {
        item.imageData?.let { base64String ->
            try {
                ImageUtils.base64ToBitmap(base64String)
            } catch (e: Exception) {
                Log.e("ItemCard", "Error loading image", e)
                null
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.Surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.itemName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = AppTheme.OnSurface
                    )
                    Text(
                        text = "Category: $displayCategory",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.OnSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Status: ${item.status}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = when (item.status) {
                            "pending" -> Color.Yellow
                            "matched" -> Color.Green
                            "found" -> Color.Blue
                            "closed" -> Color.Red
                            else -> AppTheme.OnSurface.copy(alpha = 0.7f)
                        }
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = AppTheme.Primary
                    )
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                
                // Display image if bitmap was successfully created
                bitmap?.let { btm ->
                    Image(
                        bitmap = btm.asImageBitmap(),
                        contentDescription = "Item Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Text(
                    text = "Description: ${item.description}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.OnSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Location: ${item.locationDescription}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.OnSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Date: ${item.selectedDate} ${item.selectedTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.OnSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewItemOverviewPage() {
    AppTheme {
        ItemOverviewPage()
    }
}
