package com.example.firebaseauthdemoapp.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.firebaseauthdemoapp.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
class ItemOverview : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // The content for the Item Overview Page
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
            // Tab Row for navigation between different sections
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Reported Items") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Found Items") }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Claimed Items") }
                )
            }

            // Display the content based on the selected tab
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Updated typography style
        Text("Reported Items", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("List of items that have been reported.")
    }
}

@Composable
fun FoundItemsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Updated typography style
        Text("Found Items", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("List of items that have been found.")
    }
}

@Composable
fun ClaimedItemsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Updated typography style
        Text("Claimed Items", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("List of items that have been claimed.")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewItemOverviewPage() {
    AppTheme {
        ItemOverviewPage()
    }
}
