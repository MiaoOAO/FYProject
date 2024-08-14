package com.example.fyproject.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyproject.R
import com.example.fyproject.adapter.UserListAdapter
import com.example.fyproject.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects

class AdminGetUserListFragment : Fragment(), UserListAdapter.ItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_get_user_list, container, false)

        val userSearchBtn: Button = view.findViewById(R.id.userListSearchBtn)
        val userSearchTf: TextView = view.findViewById(R.id.userListSearch)

        recyclerView = view.findViewById(R.id.userListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        // Fetch data from Firestore
        fetchDataFromFirestore()

        val history = mutableListOf<String>()
        history.add("All Users")
        history.add("Pending Approve")

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, history)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner = view.findViewById<Spinner>(R.id.userList_spinnerAdmin)
        spinner.adapter = adapter

        // handle spinner selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedOption = history[position]

                if(selectedOption == "All Users"){
                    fetchDataFromFirestore()
                }else{
                    fetchDataFromFirestoreApproveStatus()
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // do something when nothing is selected
            }
        }

        userSearchBtn.setOnClickListener {
            val userSearch = userSearchTf.text.toString()

            if (userSearch != ""){
                val collectionName = "user" // Replace with your collection name
                val userId = FirebaseAuth.getInstance().currentUser!!.uid

//        admin side --> val query = db.collection(collectionName)
                val query = db.collection(collectionName).whereEqualTo("phone", userSearch)
                query.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val dataList = task.result?.toObjects<User>() ?: emptyList()
                        setupRecyclerView(dataList)
                    } else {
                        // Handle any errors in data retrieval
                    }
                }
            }else{
                refreshData()
                val collectionName = "user" // Replace with your collection name
                val userId = FirebaseAuth.getInstance().currentUser!!.uid

//        admin side --> val query = db.collection(collectionName)
                val query = db.collection(collectionName)
                query.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val dataList = task.result?.toObjects<User>() ?: emptyList()
                        setupRecyclerView(dataList)
                    } else {
                        // Handle any errors in data retrieval
                    }
                }
            }

        }

        return view
    }

    private fun fetchDataFromFirestore() {
        val collectionName = "user" // Replace with your collection name
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

//        admin side --> val query = db.collection(collectionName)
        val query = db.collection(collectionName)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataList = task.result?.toObjects<User>() ?: emptyList()
                setupRecyclerView(dataList)
            } else {
                // Handle any errors in data retrieval
            }
        }
    }

    private fun fetchDataFromFirestoreApproveStatus() {
        val collectionName = "user" // Replace with your collection name
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

//        admin side --> val query = db.collection(collectionName)
        val query = db.collection(collectionName).whereEqualTo("approve", "0")
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataList = task.result?.toObjects<User>() ?: emptyList()
                setupRecyclerView(dataList)
            } else {
                // Handle any errors in data retrieval
            }
        }
    }

    private fun refreshData() {
        fetchDataFromFirestore()
    }


    private fun setupRecyclerView(dataList: List<User>) {
        recyclerView.adapter = UserListAdapter(dataList, this)
    }

    override fun onItemClickForUser(user: User) {
        val bundle = Bundle();

        val userId = user.userId
        bundle.putString("user_id", userId)


        val fragment = AdminGetUserDetailsFragment(); // Create new fragment
        fragment.setArguments(bundle);

        val transaction = activity?.supportFragmentManager?.beginTransaction();
        transaction?.replace(R.id.adminFragmentContainer, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

}