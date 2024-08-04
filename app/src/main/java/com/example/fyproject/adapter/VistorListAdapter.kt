package com.example.fyproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fyproject.R
import com.example.fyproject.data.visitor
import com.example.fyproject.listener.ItemClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.collection.LLRBNode
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class VistorListAdapter(private val visitorList: List<visitor>, private val listener: ItemClickListener) : RecyclerView.Adapter<VistorListAdapter.VisitorViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    inner class VisitorViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.item_name)
        val plateNo : TextView = itemView.findViewById(R.id.item_plateNo)
        val date : TextView = itemView.findViewById(R.id.item_date)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(visitorList[position])
                }
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(visitor: visitor)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VistorListAdapter.VisitorViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.visitor_list_layout, parent, false )

        return VisitorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VistorListAdapter.VisitorViewHolder, position: Int) {
        val item = visitorList[position]
        holder.name.text = item.name
        holder.plateNo.text = item.plateNo

        val visitorId = item.visitorId
//        holder.date.text = item.VisitDate

        val visitDateString = item.VisitDate  // Assuming VisitDate is a String

// Get today's date in yyyy-MM-dd format
        val formatter = SimpleDateFormat("d/M/yyyy")
        val today = formatter.format(Date())  // Use Date() for current date

// Parse visitDate and today's date into Date objects
        val visitDate: Date? = try {
            formatter.parse(visitDateString)
        } catch (e: Exception) {
            null  // Handle parsing errors (e.g., invalid format)
        }

        val isValidVisit = if (visitDate != null) {
            visitDate.after(formatter.parse(today)) || visitDate.equals(formatter.parse(today))   // Check if visitDate is after today
        } else {
            false  // Consider invalid visitDate as expired
        }

        if (isValidVisit) {
            holder.date.text = visitDateString  // Set text if visit is valid

            val query = db.collection("visitor").whereEqualTo("visitorId", visitorId)

            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result.documents) {
                        val documentReference = document.reference
                        val data = HashMap<String, Any>()
                        data["VisitDate"] = visitDateString

                        documentReference.update(data)
                            .addOnSuccessListener {
//                                    Toast.makeText(requireContext(),"Updated sucessfully", Toast.LENGTH_SHORT).show()

                            }
                            .addOnFailureListener { e ->
//                                    Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
//                        Toast.makeText(requireContext(),"Failed to retrieve documents", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Handle expired visitDate (e.g., set a different text or color)

            holder.date.text = "Expired"  // Example for expired visit

                val query = db.collection("visitor").whereEqualTo("visitorId", visitorId)

                query.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result.documents) {
                            val documentReference = document.reference
                            val data = HashMap<String, Any>()
                            data["VisitDate"] = "Expired"

                            documentReference.update(data)
                                .addOnSuccessListener {
//                                    Toast.makeText(requireContext(),"Updated sucessfully", Toast.LENGTH_SHORT).show()

                                }
                                .addOnFailureListener { e ->
//                                    Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
//                        Toast.makeText(requireContext(),"Failed to retrieve documents", Toast.LENGTH_SHORT).show()
                    }
                }
        }



//        //val cornerRadius = resources.getDimensionPixelSize(R.dimen.corner_radius) // Replace with your desired corner radius
//        val backgroundDrawable = ContextCompat.getDrawable(holder.itemView.context, R.drawable.border) // Replace with your item background drawable
//
//        if (item.status == 1) {
//            backgroundDrawable?.setTint(ContextCompat.getColor(holder.itemView.context, R.color.yellow))
//        } else {
//            backgroundDrawable?.setTint(ContextCompat.getColor(holder.itemView.context, R.color.teal_700))
//        }
//
//        holder.itemView.background = backgroundDrawable
    }

    override fun getItemCount(): Int {
        return visitorList.size
    }

}