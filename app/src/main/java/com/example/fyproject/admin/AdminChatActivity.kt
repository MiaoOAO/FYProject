package com.example.fyproject.admin

import ChatAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyproject.R
import com.example.fyproject.data.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Calendar
import java.util.TimeZone

class AdminChatActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: MutableList<ChatMessage>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val chatRef = firestore.collection("chats")

        messageList = mutableListOf()
        val currentUserId = auth.currentUser?.uid ?: ""
        chatAdapter = ChatAdapter(messageList, currentUserId)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // Listening for new messages
        chatRef.orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                for (docChange in snapshots?.documentChanges!!) {
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            val chatMessage = docChange.document.toObject(ChatMessage::class.java)
                            messageList.add(chatMessage)
                            chatAdapter.notifyItemInserted(messageList.size - 1)
                            recyclerView.scrollToPosition(messageList.size - 1)
                        }
                        else -> {}
                    }
                }
            }

        val sendButton = findViewById<Button>(R.id.sendButton)
        val messageInput = findViewById<EditText>(R.id.messageInput)

        sendButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
            val timestampInKL = calendar.timeInMillis

            val messageText = messageInput.text.toString()
            if (messageText.isNotEmpty()) {
                val messageId = chatRef.document().id
                val chatMessage = ChatMessage(
                    id = messageId,
                    message = messageText,
                    senderId = "Admin",
                    timestamp = timestampInKL
                )
                chatRef.document(messageId).set(chatMessage)
                messageInput.text.clear()
            }
        }
    }
}