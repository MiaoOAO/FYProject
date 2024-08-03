package com.example.fyproject.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyproject.R
import com.example.fyproject.adapter.VistorListAdapter
import com.example.fyproject.data.visitor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import java.text.SimpleDateFormat
import java.util.Date

class AdminVisitorCheckInFragment : Fragment(), VistorListAdapter.ItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_visitor_check_in, container, false)

        val visSearchBtn: Button = view.findViewById(R.id.visCheckInSearchBtnAdmin)
        val visSearchTf: TextView = view.findViewById(R.id.visCheckInSearchAdmin)

        val formatter = SimpleDateFormat("d/M/yyyy")
        val today = formatter.format(Date())

        recyclerView = view.findViewById(R.id.visCheckInRecyclerViewAdmin)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        fetchDataFromFirestore()

        visSearchBtn.setOnClickListener{
            val visSearch = visSearchTf.text.toString().uppercase()

            if (visSearch != ""){
                val collectionName = "visitor" // Replace with your collection name
                val userId = FirebaseAuth.getInstance().currentUser!!.uid


//        admin side --> val query = db.collection(collectionName)
                val query = db.collection(collectionName).whereEqualTo("plateNo", visSearch).whereEqualTo("VisitDate", today).whereNotEqualTo("checkInDate", today)

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
                val query = db.collection(collectionName).whereEqualTo("VisitDate", today).whereNotEqualTo("checkInDate", today)
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

        val formatter = SimpleDateFormat("d/M/yyyy")
        val today = formatter.format(Date())

//        admin side --> val query = db.collection(collectionName)
        val query = db.collection(collectionName).whereEqualTo("VisitDate", today).whereNotEqualTo("checkInDate", today)
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

        val visitorName = visitor.name
        val visitorPlate = visitor.plateNo
        val visitorVisitDate = visitor.VisitDate
        val visitorId = visitor.visitorId
        val visitorPhone = visitor.phone

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Visitor Check-in Confirmation")
            .setMessage("Visitor Name : ${visitorName} \nVisitor Plate : ${visitorPlate} \nDate : ${visitorVisitDate} \nPhone : ${visitorPhone}")
            .setPositiveButton("Approve") { dialog, which ->

                // Get the current document reference (replace with your document reference)
                val query = db.collection("visitor").whereEqualTo("visitorId", visitorId)

                query.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result.documents) {
                            val documentReference = document.reference
                            val data = HashMap<String, Any>()
                            data["checkInDate"] = visitorVisitDate

                            documentReference.update(data)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(),"Updated sucessfully", Toast.LENGTH_SHORT).show()

                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(requireContext(),"Failed to retrieve documents", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()


    }




}