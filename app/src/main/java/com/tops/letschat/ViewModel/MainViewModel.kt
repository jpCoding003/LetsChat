package com.tops.letschat.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tops.letschat.model.User

class MainViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _currentUserData = MutableLiveData<User>()
    val currentUserData: LiveData<User> = _currentUserData

    private val _logoutState = MutableLiveData<Boolean>()
    val logoutState: LiveData<Boolean> = _logoutState

    init {
        fetchUsers()
        fetchCurrentUser()
    }

    private fun fetchUsers() {
        database.getReference("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                snapshot.children.forEach { dataSnapshot ->
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null && user.uid != currentUserId) {
                        userList.add(user)
                    }
                }
                _users.postValue(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun fetchCurrentUser() {
        if (currentUserId != null) {
            database.getReference("users").child(currentUserId).get().addOnSuccessListener {
                val user = it.getValue(User::class.java)
                _currentUserData.postValue(user!!)
            }
        }
    }

    fun logout() {
        auth.signOut()
        _logoutState.postValue(true)
    }
}
