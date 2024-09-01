package com.example.fyproject

import ChatAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyproject.data.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.util.Calendar
import java.util.TimeZone


class ChatActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: MutableList<ChatMessage>
    private lateinit var auth: FirebaseAuth
    private lateinit var listenerRegistration: ListenerRegistration

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
        listenerRegistration = chatRef
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
//                    Log.w("ChatActivity", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    messageList.clear()
                    for (doc in snapshots.documents) {
                        val message = doc.toObject(ChatMessage::class.java)
                        if (message != null) {
                            messageList.add(message)
                        }
                    }
                    chatAdapter.notifyDataSetChanged()
                    // Scroll to the latest message
                    recyclerView.scrollToPosition(messageList.size - 1)
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
                    senderId = auth.currentUser?.uid ?: "",
                    timestamp = timestampInKL
                )
                chatRef.document(messageId).set(chatMessage)
                messageInput.text.clear()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove() // Remove the listener when the activity is destroyed
    }

}
