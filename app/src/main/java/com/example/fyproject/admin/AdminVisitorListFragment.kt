package com.example.fyproject.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyproject.R
import com.example.fyproject.VisitorDetailsFragment
import com.example.fyproject.adapter.VistorListAdapter
import com.example.fyproject.data.visitor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import java.text.SimpleDateFormat
import java.util.Date


class AdminVisitorListFragment : Fragment(), VistorListAdapter.ItemClickListener  {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_visitor_list, container, false)

        val visSearchBtn: Button = view.findViewById(R.id.visListSearchBtnAdmin)
        val visSearchTf: TextView = view.findViewById(R.id.visListSearchAdmin)

        recyclerView = view.findViewById(R.id.visListRecyclerViewAdmin)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        fetchDataFromFirestore()

        val history = mutableListOf<String>()
        history.add("Valid")
        history.add("Not Valid/Expired")

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, history)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner = view.findViewById<Spinner>(R.id.visitorList_spinnerAdmin)
        spinner.adapter = adapter

        // handle spinner selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedOption = history[position]

                if(selectedOption == "Valid"){
                    fetchDataFromFirestore()
                }else{
                    fetchDataFromFirestoreHistory()
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // do something when nothing is selected
            }
        }

        visSearchBtn.setOnClickListener{
            val visSearch = visSearchTf.text.toString().uppercase()

            if (visSearch != ""){
                val collectionName = "visitor" // Replace with your collection name
                val userId = FirebaseAuth.getInstance().currentUser!!.uid

//        admin side --> val query = db.collection(collectionName)
                val query = db.collection(collectionName).whereEqualTo("plateNo", visSearch)

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
                val query = db.collection(collectionName)
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
        val query = db.collection(collectionName).whereNotEqualTo("VisitDate", "Expired")
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataList = task.result?.toObjects<visitor>() ?: emptyList()
                setupRecyclerView(dataList)
            } else {
                // Handle any errors in data retrieval
            }
        }
    }

    private fun fetchDataFromFirestoreHistory() {
        val collectionName = "visitor" // Replace with your collection name
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

//        admin side --> val query = db.collection(collectionName)
        val query = db.collection(collectionName).whereEqualTo("VisitDate", "Expired")
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


        val fragment = AdminVisitorDetailsFragment(); // Create new fragment
        fragment.setArguments(bundle);

        val transaction = activity?.supportFragmentManager?.beginTransaction();
        transaction?.replace(R.id.adminFragmentContainer, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()

    }

}