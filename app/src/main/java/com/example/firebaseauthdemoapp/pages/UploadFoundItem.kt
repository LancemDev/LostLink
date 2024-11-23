package com.example.firebaseauthdemoapp.pages

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.volley.toolbox.ImageRequest
import com.example.firebaseauthdemoapp.AppViewModel
import com.example.firebaseauthdemoapp.ItemCategory
import com.example.firebaseauthdemoapp.R
import com.example.firebaseauthdemoapp.UploadItemState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import coil.compose.rememberImagePainter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadFoundItem(
    viewModel: AppViewModel = viewModel()
) {
    val context = LocalContext.current

    var uploadState by remember { mutableStateOf(UploadItemState()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var expandedCategoryMenu by remember { mutableStateOf(false) }
    val now = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf("${now.get(Calendar.DAY_OF_MONTH)}/${now.get(Calendar.MONTH) + 1}/${now.get(Calendar.YEAR)}") }
    var selectedTime by remember { mutableStateOf(String.format("%02d:%02d", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var locationDescription by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val scrollState = rememberScrollState()

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }
    val minAllowedDate = Calendar.getInstance().apply {
        add(Calendar.MONTH, -1)
    }

    var isLoading by remember { mutableStateOf(false) }
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
                title = { Text("Upload Found Item", color = AppTheme.OnPrimary) }
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
                value = uploadState.itemName,
                onValueChange = { uploadState = uploadState.copy(itemName = it) },
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
                    value = uploadState.category.name,
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
                                uploadState = uploadState.copy(category = category)
                                expandedCategoryMenu = false
                            }
                        )
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = uploadState.description,
                onValueChange = { uploadState = uploadState.copy(description = it) },
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
                    Text("When Was It found?", color = AppTheme.Primary, style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val selectedCalendar = Calendar.getInstance().apply {
                                            set(year, month, dayOfMonth)
                                        }
                                        when {
                                            selectedCalendar.after(Calendar.getInstance()) -> {
                                                Toast.makeText(context, "Cannot select a future date!", Toast.LENGTH_SHORT).show()
                                            }
                                            selectedCalendar.before(minAllowedDate) -> {
                                                Toast.makeText(context, "Date cannot be earlier than 1 month ago!", Toast.LENGTH_SHORT).show()
                                            }
                                            else -> {
                                                selectedDate = "$dayOfMonth/${month + 1}/$year"
                                                calendar.set(year, month, dayOfMonth)
                                            }
                                        }
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
                                        val selectedCalendar = Calendar.getInstance().apply {
                                            set(
                                                calendar.get(Calendar.YEAR),
                                                calendar.get(Calendar.MONTH),
                                                calendar.get(Calendar.DAY_OF_MONTH),
                                                hourOfDay,
                                                minute
                                            )
                                        }
                                        if (selectedCalendar.after(Calendar.getInstance())) {
                                            Toast.makeText(context, "Cannot select a future time!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                                        }
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


                    selectedImageUri?.let { uri ->
                        Image(
                            painter = rememberImagePainter(data = uri),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }

                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (uploadState.itemName.isBlank() || uploadState.category == null) {
                        Toast.makeText(context, "Please fill in all required fields!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    viewModel.submitFoundItem(
                        uploadState = uploadState,
                        locationDescription = locationDescription,
                        selectedDate = selectedDate,
                        selectedTime = selectedTime,
                        imageUri = selectedImageUri
                    ) { isSuccess, message ->
                        isLoading = false
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        if (isSuccess) {
                            // Reset fields on success
                            uploadState = UploadItemState()
                            locationDescription = ""
                            selectedDate = ""
                            selectedTime = ""
                            selectedImageUri = null
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.Primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = AppTheme.OnPrimary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text("Upload Found Item", color = AppTheme.OnPrimary)
            }
        }
        }
    }



@Preview(showBackground = true)
@Composable
fun PreviewUploadFoundItem() {
    UploadFoundItem()
}