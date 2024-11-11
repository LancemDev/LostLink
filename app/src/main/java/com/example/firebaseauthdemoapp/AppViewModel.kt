package com.example.firebaseauthdemoapp

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class AppViewModel : ViewModel(){
    private val db = FirebaseFirestore.getInstance()

    fun saveDataToFirestore(data: Map<String, Any>, collectionName: String, onResult: (Boolean) -> Unit) {
        db.collection(collectionName)
            .add(data)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
}