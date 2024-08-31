package com.example.fyproject.data

data class ChatMessage(
    val id: String = "",
    val message: String = "",
    val senderId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)