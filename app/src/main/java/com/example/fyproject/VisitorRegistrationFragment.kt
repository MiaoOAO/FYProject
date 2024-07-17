package com.example.fyproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.fyproject.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VisitorRegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VisitorRegistrationFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_visitor_registration, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val vBtn:Button = view.findViewById(R.id.visSubmitBtn)
        val sBtn:Button = view.findViewById(R.id.searchVisBtn)

//      add visitor registration to firestore
        vBtn.setOnClickListener{

            val vName = view.findViewById<TextView?>(R.id.visName).text.toString()
            val vPlate = view.findViewById<TextView?>(R.id.visPlateNo).text.toString()
            val vPhone = view.findViewById<TextView?>(R.id.visPhone).text.toString()
            val vDate = view.findViewById<TextView?>(R.id.visDate).text.toString()

            val visitorMap = hashMapOf(
                "name" to vName,
                "plateNo" to vPlate,
                "phone" to vPhone,
                "VisitDate" to vDate
            )

            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            fStore.collection("visitor").document(userId).set(visitorMap).addOnSuccessListener {
                Toast.makeText(requireContext(), "Visitor registered", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener{
                    Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT).show()
                }

        }


        //search vehicle plate function
        sBtn.setOnClickListener{
            // Collection reference
            val visitorRef = fStore.collection("visitor")

// Optional: Filter by plateNo (replace with your desired plate number)
            val searchPlateNo = "BGM0000"  // Replace with actual plate number
            val query = if (searchPlateNo.isNotEmpty()) {
                visitorRef.whereEqualTo("plateNo", searchPlateNo)  // Filter by plateNo
            } else {
                visitorRef  // No filter, retrieve all documents
            }

// Perform the query and handle results
            query.get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(requireContext(), "No visitors found", Toast.LENGTH_SHORT).show()
                    } else {
                        val plateNumbers = mutableListOf<String>()
                        for (document in documents) {
                            val plateNo = document.getString("plateNo")
                            if (plateNo != null) {
                                plateNumbers.add(plateNo)
                            }
                        }
                        // Do something with the list of plate numbers (e.g., display in a list)
                        Toast.makeText(requireContext(), "Plate Number: $plateNumbers found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show()
                }
        }






        return view
    }

}