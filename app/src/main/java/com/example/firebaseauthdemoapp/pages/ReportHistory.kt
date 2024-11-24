package com.example.firebaseauthdemoapp.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
//import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            TopAppBar(
                title = { Text("Report History", color = AppTheme.OnPrimary) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = AppTheme.Primary)
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
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppTheme.Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = report.itemName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = AppTheme.OnSurface
                    )
                    Text(
                        text = "Category: ${report.category}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.OnSurface.copy(alpha = 0.7f)
                    )
                    Row {
                        Text(
                            text = "Status: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppTheme.OnSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = report.status,
                            style = MaterialTheme.typography.bodyMedium,
                            color = when (report.status) {
                                "pending" -> Color.Yellow
                                "matched" -> Color.Green
                                "found" -> Color.Blue
                                "closed" -> Color.Red
                                else -> AppTheme.OnSurface.copy(alpha = 0.7f)
                            }
                        )
                    }
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
                Text(
                    text = "Description: ${report.description}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.OnSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Location: ${report.locationDescription}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.OnSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Date Lost: ${report.dateLost}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.OnSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReportHistoryPage() {
    ReportHistory()
}

