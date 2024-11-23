package com.example.firebaseauthdemoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {
    private val _uploadStatus = MutableStateFlow<UploadStatus>(UploadStatus.Idle)
    val uploadStatus = _uploadStatus.asStateFlow()

    private val _reportHistory = MutableStateFlow<List<ReportItemState>>(emptyList())
    val reportHistory = _reportHistory.asStateFlow()

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

    fun resetUploadStatus() {
        _uploadStatus.value = UploadStatus.Idle
    }

    private fun findPotentialMatches(reportId: String, report: HashMap<String, Any>) {
        // Implement your logic to find potential matches
    }
}

