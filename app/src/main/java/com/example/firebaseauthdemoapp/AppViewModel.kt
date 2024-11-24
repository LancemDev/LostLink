package com.example.firebaseauthdemoapp

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import android.util.Base64
import com.google.firebase.firestore.FieldValue

class AppViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    private val storage = Firebase.storage
    private val _uploadStatus = MutableStateFlow<UploadStatus>(UploadStatus.Idle)
    val uploadStatus: StateFlow<UploadStatus> = _uploadStatus

    private val _reportHistory = MutableStateFlow<List<ReportItemState>>(emptyList())
    val reportHistory: StateFlow<List<ReportItemState>> = _reportHistory

    private fun convertImageToBase64(context: Context, uri: Uri): String {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("AppViewModel", "Error converting image to base64", e)
            throw e
        }
    }

    fun uploadFoundItem(
        context: Context,
        reportState: UploadItemState,
        locationDescription: String,
        selectedDate: String,
        selectedTime: String,
        imageUri: Uri?
    ) = viewModelScope.launch {
        try {
            _uploadStatus.value = UploadStatus.Loading
            Log.d("AppViewModel", "Starting upload process")

            val currentUser = Firebase.auth.currentUser
            
            // Convert image to base64 if it exists
            val imageData = imageUri?.let { uri ->
                try {
                    convertImageToBase64(context, uri)
                } catch (e: Exception) {
                    Log.e("AppViewModel", "Failed to convert image", e)
                    null
                }
            }

            // Create item data including the base64 image
            val itemData = hashMapOf(
                "itemName" to reportState.itemName,
                "category" to reportState.category.name,
                "description" to reportState.description,
                "locationDescription" to locationDescription,
                "selectedDate" to selectedDate,
                "selectedTime" to selectedTime,
                "userId" to currentUser?.uid,
                "imageData" to imageData,
                "timestamp" to FieldValue.serverTimestamp()
            )

            // Upload to Firestore
            Firebase.firestore.collection("found_items")
                .add(itemData)
                .addOnSuccessListener {
                    Log.d("AppViewModel", "Document uploaded successfully")
                    viewModelScope.launch {
                        _uploadStatus.value = UploadStatus.Success
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AppViewModel", "Error uploading document", e)
                    viewModelScope.launch {
                        _uploadStatus.value = UploadStatus.Error
                    }
                }

        } catch (e: Exception) {
            Log.e("AppViewModel", "Unexpected error during upload", e)
            _uploadStatus.value = UploadStatus.Error
        }
    }

    fun fetchReportHistory() = viewModelScope.launch {
        try {
            val currentUser = Firebase.auth.currentUser
            if (currentUser != null) {
                firestore.collection("lostItemReports")
                    .whereEqualTo("userId", currentUser.uid)
                    .get()
                    .addOnSuccessListener { documents ->
                        val reports = documents.map { document ->
                            ReportItemState(
                                itemName = document.getString("itemName") ?: "",
                                category = ItemCategory.valueOf(document.getString("category") ?: "OTHER"),
                                locationDescription = document.getString("locationDescription") ?: "",
                                status = document.getString("status") ?: ""
                            )
                        }
                        _reportHistory.value = reports
                    }
                    .addOnFailureListener {
                        _reportHistory.value = emptyList()
                    }
            }
        } catch (e: Exception) {
            _reportHistory.value = emptyList()
        }
    }

    fun submitReport(
        reportState: ReportItemState,
        locationDescription: String,
        selectedDate: String,
        selectedTime: String,
        characteristics: Map<String, String>
    ) = viewModelScope.launch {
        try {
            _uploadStatus.value = UploadStatus.Loading

            val currentUser = Firebase.auth.currentUser

            val report = hashMapOf<String, Any>(
                "userId" to (currentUser?.uid ?: ""),
                "category" to reportState.category.name,
                "itemName" to reportState.itemName,
                "description" to reportState.description,
                "locationDescription" to locationDescription,
                "dateLost" to Timestamp.now(), // Combine selectedDate and selectedTime into a Timestamp
                "status" to "pending",
                "matchedWithItemId" to "", // Use an empty string or another default value
                "createdAt" to Timestamp.now()
            )

            firestore.collection("lostItemReports")
                .add(report)
                .addOnSuccessListener {
                    _uploadStatus.value = UploadStatus.Success
                    findPotentialMatches(it.id, report)
                }
                .addOnFailureListener { e ->
                    _uploadStatus.value = UploadStatus.Error
                }

        } catch (e: Exception) {
            _uploadStatus.value = UploadStatus.Error
        }
    }

    private fun findPotentialMatches(reportId: String, report: HashMap<String, Any>) {
        // Implement your logic to find potential matches
        Log.d("AppViewModel", "Finding potential matches for report: $reportId")

        // Example logic to find potential matches based on category and description
        firestore.collection("foundItems")
            .whereEqualTo("category", report["category"])
            .get()
            .addOnSuccessListener { documents ->
                val potentialMatches = documents.mapNotNull { document ->
                    val description = document.getString("description") ?: ""
                    if (description.contains(report["description"].toString(), ignoreCase = true)) {
                        document.id
                    } else {
                        null
                    }
                }

                if (potentialMatches.isNotEmpty()) {
                    Log.d("AppViewModel", "Potential matches found: $potentialMatches")
                    // Update the report with the matched item IDs
                    firestore.collection("lostItemReports").document(reportId)
                        .update("matchedWithItemId", potentialMatches.joinToString(","))
                        .addOnSuccessListener {
                            Log.d("AppViewModel", "Report updated with potential matches")
                        }
                        .addOnFailureListener { e ->
                            Log.e("AppViewModel", "Failed to update report with potential matches", e)
                        }
                } else {
                    Log.d("AppViewModel", "No potential matches found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Failed to find potential matches", e)
            }
    }

    fun resetUploadStatus() {
        _uploadStatus.value = UploadStatus.Idle
    }

//    private fun compressImage(context: Context, uri: Uri): String {
//        val inputStream = context.contentResolver.openInputStream(uri)
//        val originalBitmap = BitmapFactory.decodeStream(inputStream)
//        inputStream?.close()
//
//        // Calculate new dimensions while maintaining aspect ratio
//        val maxDimension = 1024 // Maximum width or height
//        val ratio = min(
//            maxDimension.toFloat() / originalBitmap.width,
//            maxDimension.toFloat() / originalBitmap.height
//        )
//        val width = (ratio * originalBitmap.width).toInt()
//        val height = (ratio * originalBitmap.height).toInt()
//
//        // Compress the bitmap
//        val compressedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true)
//        val outputStream = ByteArrayOutputStream()
//        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
//        val compressedBytes = outputStream.toByteArray()
//
//        return Base64.encodeToString(compressedBytes, Base64.DEFAULT)
//    }


// Use this when displaying images
//    @Composable
//    fun DisplayFoundItemImage(base64String: String?) {
//        if (base64String != null) {
//            val bitmap = remember(base64String) {
//                try {
//                    ImageUtils.base64ToBitmap(base64String)
//                } catch (e: Exception) {
//                    null
//                }
//            }
//
//            bitmap?.let {
//                Image(
//                    bitmap = it.asImageBitmap(),
//                    contentDescription = "Found Item Image",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp),
//                    contentScale = ContentScale.Crop
//                )
//            }
//        }
//    }
}


