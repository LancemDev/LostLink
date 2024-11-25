package com.example.firebaseauthdemoapp.pages

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
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
        Log.d("UploadFoundItem", "Selected Image URI: $uri")
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
            // Item Name with icon
            OutlinedTextField(
                value = reportState.itemName,
                onValueChange = { reportState = reportState.copy(itemName = it) },
                label = { Text("Item Name", color = AppTheme.TextGray) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Done,
                        contentDescription = null,
                        tint = AppTheme.Primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AppTheme.Primary,
                    unfocusedBorderColor = AppTheme.TextGray,
                    focusedLabelColor = AppTheme.Primary,
                    focusedTextColor = AppTheme.TextGray
                )
            )

            ExposedDropdownMenuBox(
                expanded = expandedCategoryMenu,
                onExpandedChange = { expandedCategoryMenu = !expandedCategoryMenu }
            ) {
                OutlinedTextField(
                    value = reportState.category.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category", color = AppTheme.TextGray) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = AppTheme.Primary
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            "Select category",
                            tint = AppTheme.Primary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppTheme.Primary,
                        unfocusedBorderColor = AppTheme.TextGray,
                        focusedTextColor = AppTheme.TextGray
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
                leadingIcon = {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = AppTheme.Primary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AppTheme.Primary,
                    unfocusedBorderColor = AppTheme.TextGray,
                    focusedTextColor = AppTheme.TextGray
                )
            )

            // Location and DateTime Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Where Was It Lost?", color = AppTheme.Primary, style = MaterialTheme.typography.titleMedium)

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
                        context = context,
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
