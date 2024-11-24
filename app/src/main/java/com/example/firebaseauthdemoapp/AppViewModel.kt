package com.example.firebaseauthdemoapp

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import java.util.UUID


class AppViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    private val storage = Firebase.storage
    private val _uploadStatus = MutableStateFlow<UploadStatus>(UploadStatus.Idle)
    val uploadStatus: StateFlow<UploadStatus> = _uploadStatus

    private val _reportHistory = MutableStateFlow<List<ReportItemState>>(emptyList())
    val reportHistory = _reportHistory.asStateFlow()

    fun submitFoundItem(
        uploadState: UploadItemState,
        locationDescription: String,
        selectedDate: String,
        selectedTime: String,
        imageUri: Uri?,
        onResult: (Boolean, String) -> Unit
    ) = viewModelScope.launch {
        try {
            _uploadStatus.value = UploadStatus.Loading

            val currentUser = Firebase.auth.currentUser

            val foundItem = hashMapOf<String, Any>(
                "userId" to (currentUser?.uid ?: ""),
                "category" to uploadState.category.name,
                "itemName" to uploadState.itemName,
                "description" to uploadState.description,
                "locationDescription" to locationDescription,
                "dateFound" to Timestamp.now(), // Combine selectedDate and selectedTime into a Timestamp
                "status" to "pending",
                "createdAt" to Timestamp.now()
            )

            if (imageUri != null) {
                // Upload the image to Firebase Storage

                val fileName = UUID.randomUUID().toString()
                val storageRef = storage.reference.child("found_items/$fileName")

                storageRef.putFile(imageUri)
                    .addOnSuccessListener { taskSnapshot ->
                        storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            foundItem["imageUrl"] = downloadUri.toString()

                            // Add the found item to Firestore
                            firestore.collection("foundItems")
                                .add(foundItem)
                                .addOnSuccessListener {
                                    _uploadStatus.value = UploadStatus.Success
                                    onResult(true, "Found item submitted successfully!")
                                }
                                .addOnFailureListener { e ->
                                    _uploadStatus.value = UploadStatus.Error
                                    onResult(false, "Failed to submit found item: ${e.message}")
                                }
                        }.addOnFailureListener { e ->
                            _uploadStatus.value = UploadStatus.Error
                            onResult(false, "Failed to retrieve image URL: ${e.message}")
                        }
                    }.addOnFailureListener { e ->
                        _uploadStatus.value = UploadStatus.Error
                        onResult(false, "Failed to upload image: ${e.message}")
                    }
            } else {
                // If no image, directly add the found item to Firestore
                firestore.collection("foundItems")
                    .add(foundItem)
                    .addOnSuccessListener {
                        _uploadStatus.value = UploadStatus.Success
                        onResult(true, "Found item submitted successfully!")
                    }
                    .addOnFailureListener { e ->
                        _uploadStatus.value = UploadStatus.Error
                        onResult(false, "Failed to submit found item: ${e.message}")
                    }
            }
        } catch (e: Exception) {
            _uploadStatus.value = UploadStatus.Error
            onResult(false, "An unexpected error occurred: ${e.message}")
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
                                description = document.getString("description") ?: ""
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

    fun uploadFoundItem(
        reportState: UploadItemState,
        locationDescription: String,
        selectedDate: String,
        selectedTime: String,
        imageUri: Uri?
    ) = viewModelScope.launch {
        try {
            _uploadStatus.value = UploadStatus.Loading

            val currentUser = Firebase.auth.currentUser
            val imageUrls = mutableListOf<String>()

            imageUri?.let { uri ->
                val storageRef = storage.reference.child("found_items/${UUID.randomUUID()}")
                val uploadTask = storageRef.putFile(uri)
                uploadTask.addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        imageUrls.add(downloadUri.toString())
                        saveFoundItem(reportState, locationDescription, selectedDate, selectedTime, imageUrls, currentUser?.uid)
                    }.addOnFailureListener {
                        _uploadStatus.value = UploadStatus.Error
                    }
                }.addOnFailureListener {
                    _uploadStatus.value = UploadStatus.Error
                }
            } ?: saveFoundItem(reportState, locationDescription, selectedDate, selectedTime, imageUrls, currentUser?.uid)

        } catch (e: Exception) {
            _uploadStatus.value = UploadStatus.Error
        }
    }

    private fun saveFoundItem(
        reportState: UploadItemState,
        locationDescription: String,
        selectedDate: String,
        selectedTime: String,
        imageUrls: List<String>,
        userId: String?
    ) {
        val foundItem = hashMapOf(
            "name" to reportState.itemName,
            "category" to reportState.category.name,
            "description" to reportState.description,
            "location" to locationDescription,
            "dateFound" to Timestamp.now(), // Combine selectedDate and selectedTime into a Timestamp
            "imageUrls" to imageUrls,
            "status" to "available",
            "addedBy" to (userId ?: ""),
            "claimedBy" to null,
            "characteristics" to emptyMap<String, String>(),
            "createdAt" to Timestamp.now()
        )

        firestore.collection("foundItems")
            .add(foundItem)
            .addOnSuccessListener {
                _uploadStatus.value = UploadStatus.Success
            }
            .addOnFailureListener {
                _uploadStatus.value = UploadStatus.Error
            }
    }

    fun resetUploadStatus() {
        _uploadStatus.value = UploadStatus.Idle
    }

    private fun findPotentialMatches(reportId: String, report: HashMap<String, Any>) {
        // Implement your logic to find potential matches
    }
}


