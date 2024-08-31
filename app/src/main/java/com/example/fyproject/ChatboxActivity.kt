package com.example.fyproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ChatboxActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatLog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbox)

        editText = findViewById(R.id.editText)
        sendButton = findViewById(R.id.sendButton)
        chatLog = findViewById(R.id.chatLog)

        sendButton.setOnClickListener {
            // Get the current KL time
            val timestampText = getCurrentTimeInKL()


            val message = editText.text.toString()
            if (message.isNotEmpty()) {
                chatLog.append("User ($timestampText): $message\n")
                // Here you can add your logic to send the message to the server or handle it locally
                editText.text.clear()

            }
        }
    }

    fun getCurrentTimeInKL(): String {
        val currentTime = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"))
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return currentTime.format(formatter)
    }
}