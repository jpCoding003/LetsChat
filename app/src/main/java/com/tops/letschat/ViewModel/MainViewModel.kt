package com.tops.letschat.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tops.letschat.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid!!

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _friendRequests = MutableLiveData<List<User>>()
    val friendRequests: LiveData<List<User>> = _friendRequests

    private val _currentUserData = MutableLiveData<User>()
    val currentUserData: LiveData<User> = _currentUserData

    private val _logoutState = MutableLiveData<Boolean>()
    val logoutState: LiveData<Boolean> = _logoutState

    private val _friends = MutableLiveData<List<User>>()
    val friends: LiveData<List<User>> = _friends

    init {
        fetchUsers()
        fetchFriendRequests()
        fetchFriends()
        fetchCurrentUser()
    }

    private fun fetchFriends() {
        val friendsRef = database.getReference("friends").child(currentUserId)
        friendsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendIds = snapshot.children.mapNotNull { it.key }
                if (friendIds.isNotEmpty()) {
                    loadUsersByIds(friendIds) { users ->
                        _friends.postValue(users)
                    }
                } else {
                    _friends.postValue(emptyList())
                }
            }
            override fun onCancelled(error: DatabaseError) { /* Handle error */ }
        })
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

    private fun fetchFriendRequests() {
        val requestsRef = database.getReference("friend_requests").child(currentUserId)
        requestsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requestSenderIds = snapshot.children
                    .filter { it.child("request_type").value == "received" }
                    .map { it.key!! }

                if (requestSenderIds.isNotEmpty()) {
                    loadUsersByIds(requestSenderIds) { users ->
                        _friendRequests.postValue(users)
                    }
                } else {
                    _friendRequests.postValue(emptyList())
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendFriendRequest(receiverId: String) = viewModelScope.launch {
        // Path for sender
        database.getReference("friend_requests").child(currentUserId).child(receiverId)
            .child("request_type").setValue("sent").await()
        // Path for receiver
        database.getReference("friend_requests").child(receiverId).child(currentUserId)
            .child("request_type").setValue("received").await()
    }

    fun acceptFriendRequest(senderId: String) = viewModelScope.launch {
        // Add to friends list for both users
        database.getReference("friends").child(currentUserId).child(senderId).setValue(true).await()
        database.getReference("friends").child(senderId).child(currentUserId).setValue(true).await()

        // Remove the request from the database for both users
        removeFriendRequest(senderId)
    }

    fun declineFriendRequest(senderId: String) = viewModelScope.launch {
        removeFriendRequest(senderId)
    }

    private suspend fun removeFriendRequest(senderId: String) {
        database.getReference("friend_requests").child(currentUserId).child(senderId).removeValue().await()
        database.getReference("friend_requests").child(senderId).child(currentUserId).removeValue().await()
    }

    private fun loadUsersByIds(ids: List<String>, onResult: (List<User>) -> Unit) {
        val usersRef = database.getReference("users")
        val userList = mutableListOf<User>()
        var counter = 0
        ids.forEach { id ->
            usersRef.child(id).get().addOnSuccessListener { snapshot ->
                snapshot.getValue(User::class.java)?.let { userList.add(it) }
                counter++
                if (counter == ids.size) {
                    onResult(userList)
                }
            }
        }
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
