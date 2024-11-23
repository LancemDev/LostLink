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


data class Item(
    val title: String,
    val description: String,
    val status: String
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
    val items = listOf(
        Item("Item 3", "Description of Item 3", "Found"),
        Item("Item 4", "Description of Item 4", "Found")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Found Items", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        items.forEach { item ->
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(item.title, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Status: ${item.status}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
