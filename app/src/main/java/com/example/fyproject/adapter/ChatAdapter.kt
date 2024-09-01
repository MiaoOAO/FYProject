
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.fyproject.R
import com.example.fyproject.data.ChatMessage
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChatAdapter(
    private val messages: List<ChatMessage>,
    private val currentUserId: String
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = messages[position]
        holder.bind(chatMessage)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderTextView: TextView = itemView.findViewById(R.id.senderTextView)
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)

        fun bind(chatMessage: ChatMessage) {

            val displaySenderId = if (chatMessage.senderId == currentUserId) {
                "You"
            } else {
                chatMessage.senderId
            }

            senderTextView.text = displaySenderId
            senderTextView.setTypeface(null, Typeface.BOLD)
            messageTextView.text = chatMessage.message

            // Format the timestamp to include both date and time
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val date = Date(chatMessage.timestamp)
            timestampTextView.text = sdf.format(date)

            // Set long click listener to trigger edit/delete options
            itemView.setOnLongClickListener {
                val currentTime = Calendar.getInstance().timeInMillis
                val oneHourInMillis = 3600000L

                if (chatMessage.senderId == currentUserId && currentTime - chatMessage.timestamp <= oneHourInMillis) {
                    showEditDeleteDialog(chatMessage)
                }

                true // Return true to indicate the long click was handled
            }
        }

        private fun showEditDeleteDialog(chatMessage: ChatMessage) {
            val options = arrayOf("Edit", "Delete")

            AlertDialog.Builder(itemView.context)
                .setTitle("Select Action")
                .setItems(options) { dialog, which ->
                    when (which) {
                        0 -> showEditDialog(chatMessage)
                        1 -> deleteMessage(chatMessage)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        private fun showEditDialog(chatMessage: ChatMessage) {
            val editText = EditText(itemView.context)
            editText.setText(chatMessage.message)

            AlertDialog.Builder(itemView.context)
                .setTitle("Edit Message")
                .setView(editText)
                .setPositiveButton("Save") { _, _ ->
                    val newMessageText = editText.text.toString()
                    if (newMessageText.isNotEmpty()) {
                        val firestore = FirebaseFirestore.getInstance()
                        firestore.collection("chats")
                            .document(chatMessage.id)
                            .update("message", newMessageText)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        private fun deleteMessage(chatMessage: ChatMessage) {
            AlertDialog.Builder(itemView.context)
                .setTitle("Delete Message")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Delete") { _, _ ->
                    val firestore = FirebaseFirestore.getInstance()
                    firestore.collection("chats")
                        .document(chatMessage.id)
                        .delete()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}