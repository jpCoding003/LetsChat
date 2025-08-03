package com.tops.letschat.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")
    private val storage = FirebaseStorage.getInstance().getReference("profile_images")
    private val currentUserId = auth.currentUser?.uid!!

    fun updateStatus(newStatus: String) = viewModelScope.launch {
        database.child(currentUserId).child("status").setValue(newStatus).await()
    }

    fun updateProfileImage(imageUri: Uri) = viewModelScope.launch {
        try {
            // 1. Upload the image to Firebase Storage
            val imageRef = storage.child(currentUserId)
            imageRef.putFile(imageUri).await()

            // 2. Get the download URL of the uploaded image
            val downloadUrl = imageRef.downloadUrl.await().toString()

            // 3. Update the profileImageUrl in the Realtime Database
            database.child(currentUserId).child("profileImageUrl").setValue(downloadUrl).await()

        } catch (e: Exception) {
            // Handle exceptions like network errors
        }
    }
}