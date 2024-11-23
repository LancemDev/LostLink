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
import com.example.firebaseauthdemoapp.UploadStatus
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadFoundItem(
    viewModel: AppViewModel = viewModel()
) {
    val context = LocalContext.current

    var reportState by remember { mutableStateOf(UploadItemState()) }
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

    val uploadStatus by viewModel.uploadStatus.collectAsState()

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

                    selectedImageUri?.let {
                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it)
                                .crossfade(true)
                                .build(),
                            contentScale = ContentScale.Crop
                        )

                        Image(
                            painter = painter,
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(top = 8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    viewModel.uploadFoundItem(
                        reportState = reportState,
                        locationDescription = locationDescription,
                        selectedDate = selectedDate,
                        selectedTime = selectedTime,
                        imageUri = selectedImageUri
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.Primary)
            ) {
                Text("Upload Found Item", color = AppTheme.OnPrimary)
            }
        }
    }

    // Show success or error dialog based on upload status
    when (uploadStatus) {
        UploadStatus.Success -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetUploadStatus() },
                title = { Text("Success") },
                text = { Text("Item uploaded successfully!") },
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
                text = { Text("Failed to upload item. Please try again.") },
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
fun PreviewUploadFoundItem() {
    UploadFoundItem()
}