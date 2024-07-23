package com.example.fyproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VisitorDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VisitorDetailsFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_visitor_details, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val visName = view.findViewById<TextView>(R.id.visDetailsName)
        val visPlate = view.findViewById<TextView>(R.id.visDetailsPlate)
        val visOwner = view.findViewById<TextView>(R.id.visDetailsOwner)
        val visDate = view.findViewById<TextView>(R.id.visDetailsVisitDate)

        val selectedVisitor = arguments?.getString("visitor_id")

        visName.text = selectedVisitor

        if (selectedVisitor != null) {
            val docRef = fStore.collection("visitor").whereEqualTo("visitorId", selectedVisitor)
            docRef.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val data = document.data

                    if (data != null) {
                        val plate = data["plateNo"] as String
                        val owner = data["ownerId"] as String

                        // Update UI elements with retrieved data
                        visPlate.text = plate
                        visOwner.text = owner


                        Toast.makeText(requireContext(),"I got the result in database", Toast.LENGTH_SHORT).show()


                        Toast.makeText(requireContext(), "Plate: $plate", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error, data retrieve from fireStore failed", Toast.LENGTH_SHORT).show()
            }



        }else{
            visName.text = "not found"
        }

        return view
    }

    private fun getVisitorData(text: String) {

        val docRef = fStore.collection("visitor").document(text)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val data = document.data

                if (data != null) {
                    val plate = data["plateNo"] as String


                    // Update UI elements with retrieved data
//                    visPlate.text = plate


                    Toast.makeText(requireContext(), "Plate: $plate", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { exception ->

            Toast.makeText(requireContext(), "Error, data retrieve from fireStore failed", Toast.LENGTH_SHORT).show()
        }
    }


}