package com.tops.letschat.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tops.letschat.model.Message

class ChatViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val currentUserId = auth.currentUser?.uid!!

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    fun sendMessage(receiverId: String, text: String) {
        if (text.isBlank()) return

        val timestamp = System.currentTimeMillis()
        val message = Message(currentUserId, receiverId, text, timestamp)

        // Create a unique chat room ID for the two users
        val chatRoomId = getChatRoomId(currentUserId, receiverId)

        database.getReference("chats").child(chatRoomId).push().setValue(message)
    }

    fun loadMessages(receiverId: String) {
        val chatRoomId = getChatRoomId(currentUserId, receiverId)
        val messagesRef = database.getReference("chats").child(chatRoomId)

        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = mutableListOf<Message>()
                snapshot.children.forEach {
                    val message = it.getValue(Message::class.java)
                    if (message != null) {
                        messageList.add(message)
                    }
                }
                _messages.postValue(messageList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun getChatRoomId(user1: String, user2: String): String {
        // Create a consistent chat room ID regardless of who is sender/receiver
        return if (user1 < user2) "$user1-$user2" else "$user2-$user1"
    }
}
