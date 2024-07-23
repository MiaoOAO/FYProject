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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VisitorListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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

        visSearchBtn.setOnClickListener{
            val visSearch = visSearchTf.text.toString()


            val collectionName = "visitor" // Replace with your collection name
            val userId = FirebaseAuth.getInstance().currentUser!!.uid

//        admin side --> val query = db.collection(collectionName)
            val query = db.collection(collectionName).whereEqualTo("plateNo", visSearch).whereEqualTo("ownerId", userId)
            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataList = task.result?.toObjects<visitor>() ?: emptyList()
                    setupRecyclerView(dataList)
                } else {
                    // Handle any errors in data retrieval
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

    private fun setupRecyclerView(dataList: List<visitor>) {
        recyclerView.adapter = VistorListAdapter(dataList, this)
    }

    override fun onItemClick(visitor: visitor) {

        val selectedVisitor = arguments?.getSerializable("selectedVisitor") as? visitor
        if (selectedVisitor != null) {
            Toast.makeText(requireContext(),"I got the result", Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            bundle.putString("plateNo", selectedVisitor.plateNo)
//            bundle.putSerializable("selectedVisitor", visitor) // Assuming visitor is Serializable
            VisitorDetailsFragment().arguments = bundle

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, VisitorDetailsFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
            // ... rest of your code
        } else {
            Toast.makeText(requireContext(),"nothing", Toast.LENGTH_SHORT).show()
        }
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