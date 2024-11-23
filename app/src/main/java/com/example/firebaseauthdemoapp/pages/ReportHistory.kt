package com.example.firebaseauthdemoapp.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firebaseauthdemoapp.AppViewModel
import com.example.firebaseauthdemoapp.ReportItemState
import com.example.firebaseauthdemoapp.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportHistory(viewModel: AppViewModel = viewModel()) {
    val reportHistory by viewModel.reportHistory.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchReportHistory()
    }

    Scaffold(
        containerColor = AppTheme.Background,
        topBar = {
            TopAppBar(title = { Text("Report History", color = AppTheme.OnPrimary) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reportHistory) { report ->
                ReportItem(report)
            }
        }
    }
}

@Composable
fun ReportItem(report: ReportItemState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppTheme.Background)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Item Name: ${report.itemName}", style = MaterialTheme.typography.bodyLarge)
            Text("Category: ${report.category}", style = MaterialTheme.typography.bodyMedium)
            Text("Description: ${report.description}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReportHistoryPage() {
    ReportHistory()
}

