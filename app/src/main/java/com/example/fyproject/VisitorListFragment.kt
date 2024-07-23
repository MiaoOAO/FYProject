package com.example.fyproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyproject.adapter.VistorListAdapter
import com.example.fyproject.data.visitor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import java.io.Serializable

class VisitorListFragment : Fragment(), VistorListAdapter.ItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_visitor_list, container, false)

        val visSearchBtn: Button = view.findViewById(R.id.visListSearchBtn)
        val visSearchTf: TextView = view.findViewById(R.id.visListSearch)

        recyclerView = view.findViewById(R.id.visListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch data from Firestore
        fetchDataFromFirestore()

        visSearchBtn.setOnClickListener {
            val visSearch = visSearchTf.text.toString()

            if (visSearch != ""){
            val collectionName = "visitor" // Replace with your collection name
            val userId = FirebaseAuth.getInstance().currentUser!!.uid

//        admin side --> val query = db.collection(collectionName)
            val query = db.collection(collectionName).whereEqualTo("plateNo", visSearch)
                .whereEqualTo("ownerId", userId)
            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataList = task.result?.toObjects<visitor>() ?: emptyList()
                    setupRecyclerView(dataList)
                } else {
                    // Handle any errors in data retrieval
                }
            }
        }else{
                refreshData()
                val collectionName = "visitor" // Replace with your collection name
                val userId = FirebaseAuth.getInstance().currentUser!!.uid

//        admin side --> val query = db.collection(collectionName)
                val query = db.collection(collectionName).whereEqualTo("ownerId", userId)
                query.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val dataList = task.result?.toObjects<visitor>() ?: emptyList()
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
        val collectionName = "visitor" // Replace with your collection name
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

//        admin side --> val query = db.collection(collectionName)
        val query = db.collection(collectionName).whereEqualTo("ownerId", userId)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataList = task.result?.toObjects<visitor>() ?: emptyList()
                setupRecyclerView(dataList)
            } else {
                // Handle any errors in data retrieval
            }
        }
    }

    private fun refreshData() {
        fetchDataFromFirestore()
    }


    private fun setupRecyclerView(dataList: List<visitor>) {
        recyclerView.adapter = VistorListAdapter(dataList, this)
    }

    override fun onItemClick(visitor: visitor) {
            val bundle = Bundle();

            val visitorId = visitor.visitorId
            bundle.putString("visitor_id", visitorId)


            val fragment = VisitorDetailsFragment(); // Create new fragment
            fragment.setArguments(bundle);

            val transaction = activity?.supportFragmentManager?.beginTransaction();
            transaction?.replace(R.id.fragmentContainer, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()

//        val bundle = Bundle()
//        bundle.putSerializable("selectedVisitor", visitor) // Assuming visitor is Serializable
//        VisitorDetailsFragment().arguments = bundle
//
//        val transaction = activity?.supportFragmentManager?.beginTransaction()
//        transaction?.replace(R.id.fragmentContainer, VisitorDetailsFragment())
//        transaction?.addToBackStack(null)
//        transaction?.commit()
    }


}