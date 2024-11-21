package com.example.firebaseauthdemoapp.pages

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.firebaseauthdemoapp.AppViewModel
import com.example.firebaseauthdemoapp.ItemCategory
import com.example.firebaseauthdemoapp.ReportItemState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPage(
    viewModel: AppViewModel = viewModel(),
    fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
) {
    val context = LocalContext.current
    val locationPermissionGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    var reportState by remember { mutableStateOf(ReportItemState()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var expandedCategoryMenu by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var locationDescription by remember { mutableStateOf("") }
    var locationLatLng by remember { mutableStateOf<String?>(null) }

    val calendar = Calendar.getInstance()
    val scrollState = rememberScrollState()

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    LaunchedEffect(locationPermissionGranted) {
        if (!locationPermissionGranted) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppTheme.Background)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Last Seen Location", color = AppTheme.Primary, style = MaterialTheme.typography.titleMedium)

                    Button(
                        onClick = {
                            if (locationPermissionGranted) {
                                // Getting the current location
                                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                    location?.let {
                                        locationLatLng = "Lat: ${it.latitude}, Lng: ${it.longitude}"
                                        locationDescription = "Last known location fetched."
                                    }
                                }
                            } else {
                                locationDescription = "Permission not granted to access location."
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AppTheme.Primary)
                    ) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Get Current Location", color = AppTheme.OnPrimary)
                    }

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
                    locationLatLng?.let {
                        Text("Location: $it", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

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

            // Image Upload Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppTheme.Background)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Item Image", color = AppTheme.Primary, style = MaterialTheme.typography.titleMedium)

                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AppTheme.Primary)
                    ) {
                        Icon(Icons.Filled.AccountBox, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Pick an Image", color = AppTheme.OnPrimary)
                    }

                    selectedImageUri?.let {
                        Image(painter = rememberImagePainter(it), contentDescription = "Selected Image")
                    }
                }
            }

            // Submit Button
            Button(
                onClick = { /* Submit report action */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.Primary)
            ) {
                Text("Submit Report", color = AppTheme.OnPrimary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReportPage() {
    ReportPage()
}

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
