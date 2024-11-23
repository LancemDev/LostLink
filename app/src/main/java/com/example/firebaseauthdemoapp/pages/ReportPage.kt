package com.example.firebaseauthdemoapp.pages

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firebaseauthdemoapp.AppViewModel
import com.example.firebaseauthdemoapp.ItemCategory
import com.example.firebaseauthdemoapp.ReportItemState
import com.example.firebaseauthdemoapp.AppTheme
import com.example.firebaseauthdemoapp.UploadStatus
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPage(
    viewModel: AppViewModel = viewModel(),
    fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
) {
    val context = LocalContext.current as Activity

    var reportState by remember { mutableStateOf(ReportItemState()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var expandedCategoryMenu by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var locationDescription by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val scrollState = rememberScrollState()

    val uploadStatus by viewModel.uploadStatus.collectAsState()

    Scaffold(
        containerColor = AppTheme.Background,
        topBar = {
            SmallTopAppBar(
                title = { Text("Find your Lost Item", color = AppTheme.OnPrimary) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
                .padding(bottom = 56.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Item Name
            OutlinedTextField(
                value = reportState.itemName,
                onValueChange = { reportState = reportState.copy(itemName = it) },
                label = { Text("Item Name", color = AppTheme.TextGray) },
                placeholder = { Text("Enter item name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AppTheme.Primary,
                    unfocusedBorderColor = AppTheme.TextGray,
                    focusedLabelColor = AppTheme.Primary
                )
            )

            // Category Selector
            ExposedDropdownMenuBox(
                expanded = expandedCategoryMenu,
                onExpandedChange = { expandedCategoryMenu = !expandedCategoryMenu }
            ) {
                OutlinedTextField(
                    value = reportState.category.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category", color = AppTheme.TextGray) },
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "Select category") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppTheme.Primary,
                        unfocusedBorderColor = AppTheme.TextGray,
                        focusedLabelColor = AppTheme.Primary
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoryMenu,
                    onDismissRequest = { expandedCategoryMenu = false }
                ) {
                    ItemCategory.values().forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                reportState = reportState.copy(category = category)
                                expandedCategoryMenu = false
                            }
                        )
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = reportState.description,
                onValueChange = { reportState = reportState.copy(description = it) },
                label = { Text("Description", color = AppTheme.TextGray) },
                placeholder = { Text("Describe the item in detail") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AppTheme.Primary,
                    unfocusedBorderColor = AppTheme.TextGray,
                    focusedLabelColor = AppTheme.Primary
                )
            )

            // Location Section
            OutlinedTextField(
                value = locationDescription,
                onValueChange = { locationDescription = it },
                label = { Text("Location Description", color = AppTheme.TextGray) },
                placeholder = { Text("e.g., Near the library entrance") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AppTheme.Primary,
                    unfocusedBorderColor = AppTheme.TextGray,
                    focusedLabelColor = AppTheme.Primary
                )
            )

            // Date and Time
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppTheme.Background)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("When Was It Lost?", color = AppTheme.Primary, style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        selectedDate = "$dayOfMonth/${month + 1}/$year"
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AppTheme.Primary)
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Set Date", color = AppTheme.OnPrimary)
                        }

                        Spacer(Modifier.width(8.dp))

                        Button(
                            onClick = {
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        selectedTime = "$hourOfDay:$minute"
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                                ).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AppTheme.Primary)
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Set Time", color = AppTheme.OnPrimary)
                        }
                    }
                    Text("Date: $selectedDate", color = AppTheme.TextGray)
                    Text("Time: $selectedTime", color = AppTheme.TextGray)
                }
            }

            // Submit Button
            Button(
                onClick = {
                    viewModel.submitReport(
                        reportState = reportState,
                        locationDescription = locationDescription,
                        selectedDate = selectedDate,
                        selectedTime = selectedTime,
                        characteristics = emptyMap() // Removed characteristics part
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.Primary)
            ) {
                Text("Submit Report", color = AppTheme.OnPrimary)
            }
        }
    }

    // Show success or error dialog based on upload status
    when (uploadStatus) {
        UploadStatus.Success -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetUploadStatus() },
                title = { Text("Success") },
                text = { Text("Report submitted successfully!") },
                confirmButton = {
                    Button(onClick = { viewModel.resetUploadStatus() }) {
                        Text("OK")
                    }
                }
            )
        }
        UploadStatus.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetUploadStatus() },
                title = { Text("Error") },
                text = { Text("Failed to submit report. Please try again.") },
                confirmButton = {
                    Button(onClick = { viewModel.resetUploadStatus() }) {
                        Text("OK")
                    }
                }
            )
        }
        else -> {}
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReportPage() {
    ReportPage()
}

