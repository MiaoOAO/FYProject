package com.example.fyproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyproject.R
import com.example.fyproject.data.User
import com.example.fyproject.listener.ItemClickListener
import com.google.firebase.firestore.FirebaseFirestore

class UserListAdapter(private val userList: List<User>, private val listener: ItemClickListener): RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    inner class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val userName : TextView = itemView.findViewById(R.id.item_userName)
        val userPlateNo : TextView = itemView.findViewById(R.id.item_userPlateNo)
        val userPhone : TextView = itemView.findViewById(R.id.item_userPhone)
        val userAddress: TextView = itemView.findViewById(R.id.item_userAddress)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClickForUser(userList[position])
                }
            }
        }
    }

    interface ItemClickListener {
        fun onItemClickForUser(user: User)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserListAdapter.UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_list_layout, parent, false )

        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserListAdapter.UserViewHolder, position: Int) {
        val item = userList[position]
        holder.userName.text = item.name
        holder.userPlateNo.text = item.plateNo
        holder.userPhone.text = item.phone
        holder.userAddress.text = item.address
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}