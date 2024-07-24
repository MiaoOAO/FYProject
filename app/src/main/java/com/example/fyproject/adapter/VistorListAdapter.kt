package com.example.fyproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fyproject.R
import com.example.fyproject.data.visitor
import com.example.fyproject.listener.ItemClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import org.w3c.dom.Text

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
        holder.date.text = item.VisitDate

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