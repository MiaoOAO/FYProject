package com.example.fyproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyproject.adapter.VistorListAdapter
import com.example.fyproject.data.visitor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VisitorListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VisitorListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_visitor_list, container, false)

        recyclerView = view.findViewById(R.id.visListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch data from Firestore
        fetchDataFromFirestore()

        return view
    }

    private fun fetchDataFromFirestore() {
        val collectionName = "visitor" // Replace with your collection name
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

    private fun setupRecyclerView(dataList: List<visitor>) {
        recyclerView.adapter = VistorListAdapter(dataList)
    }

}