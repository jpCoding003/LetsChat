package com.tops.letschat.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val status: String = "Hey there! I am using FirebaseChatApp."
)
