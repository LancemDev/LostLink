package com.example.firebaseauthdemoapp.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firebaseauthdemoapp.AppViewModel
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.util.Date
import java.text.SimpleDateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.firebaseauthdemoapp.ItemCategory
import com.example.firebaseauthdemoapp.ReportItemState

val Coral = Color(0xFFDA7756)
val Peachy = Color(0xFFEDCDBF)

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPage(
    viewModel: AppViewModel = viewModel()
) {
    var reportState by remember { mutableStateOf(ReportItemState()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var expandedCategoryMenu by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            SmallTopAppBar(
                title = { Text("Report Lost Item", color = Color.Black) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 56.dp), // Padding for bottom navbar
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Item Name
            OutlinedTextField(
                value = reportState.itemName,
                onValueChange = { reportState = reportState.copy(itemName = it) },
                label = { Text("Item Name") },
                placeholder = { Text("Enter item name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Coral,
                    unfocusedBorderColor = Peachy
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
                    label = { Text("Category") },
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "Select category") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Coral,
                        unfocusedBorderColor = Peachy
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
                label = { Text("Description") },
                placeholder = { Text("Describe the item in detail") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Coral,
                    unfocusedBorderColor = Peachy
                )
            )

            // Location Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Peachy)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Last Seen Location", color = Color.Black, style = MaterialTheme.typography.titleMedium)

                    Button(
                        onClick = { /* Implement location picker */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Coral)
                    ) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Pick Location")
                    }

                    OutlinedTextField(
                        value = reportState.locationDescription,
                        onValueChange = { reportState = reportState.copy(locationDescription = it) },
                        label = { Text("Location Description") },
                        placeholder = { Text("e.g., Near the library entrance") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Coral,
                            unfocusedBorderColor = Peachy
                        )
                    )
                }
            }

            // Date and Time
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Peachy)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("When Was It Lost?", color = Color.Black, style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Coral)
                        ) {
                            Icon(Icons.Filled.DateRange, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Set Date")
                        }

                        Spacer(Modifier.width(8.dp))

                        Button(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Coral)
                        ) {
                            Icon(Icons.Filled.DateRange, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Set Time")
                        }
                    }
                }
            }

            // Image Upload Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Peachy)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Add Images", color = Color.Black, style = MaterialTheme.typography.titleMedium)

                    Button(
                        onClick = { /* Launch image picker */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Coral)
                    ) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Upload Images")
                    }
                }
            }

            // Submit Button
            Button(
                onClick = { /* Submit report */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Coral)
            ) {
                Text("Submit Report")
            }
        }
    }
}
