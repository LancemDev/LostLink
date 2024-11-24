package com.example.firebaseauthdemoapp.pages

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Box(modifier = Modifier.fillMaxSize()) {
        // Animated Background Gradient
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFF0EA),
                        Color(0xFFFFE4D6),
                        Color(0xFFFFD4C2),
                        Color(0xFFFFF0EA)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                SmallTopAppBar(
                    title = {
                        Text(
                            "Report Lost Item",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = AppTheme.Primary
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.White.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier
                        .shadow(8.dp)
                        .blur(2.dp)
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Welcome Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .shadow(
                            elevation = 8.dp,
                            spotColor = AppTheme.Primary.copy(alpha = 0.25f)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Let's Find Your Item",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.Primary
                        )
                        Text(
                            "Fill in the details below to help us locate your lost item.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppTheme.TextGray
                        )
                    }
                }

                // Form Fields in Cards
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
                                focusedLabelColor = AppTheme.Primary
                            )
                        )

                        // Enhanced Category Selector
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
                                    unfocusedBorderColor = AppTheme.TextGray
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

                        // Enhanced Description field
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
                                unfocusedBorderColor = AppTheme.TextGray
                            )
                        )
                    }
                }

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

                // Submit Button with animation
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppTheme.Primary
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Submit Report",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
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

