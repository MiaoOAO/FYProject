package com.example.fyproject

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.text.util.Linkify

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
        val visCheckin = view.findViewById<TextView>(R.id.visCheckin)
        val visDate = view.findViewById<TextView>(R.id.visDetailsVisitDate)
        val visPhone = view.findViewById<TextView>(R.id.visDetailsPhone)
        val visDelete = view.findViewById<Button>(R.id.visDetailsDelButton)

        val selectedVisitor = arguments?.getString("visitor_id")

//        visName.text = selectedVisitor

        if (selectedVisitor != null) {
            val docRef = fStore.collection("visitor").whereEqualTo("visitorId", selectedVisitor)
            docRef.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val data = document.data

                    if (data != null) {
                        val plate = data["plateNo"] as String
                        val visitDate = data["VisitDate"] as String
                        val checkin = data["checkInDate"] as String
                        val name = data["name"] as String
                        val phone = data["phone"] as String

                        // Update UI elements with retrieved data
                        visPlate.text = plate
                        visDate.text = visitDate
                        visCheckin.text = checkin
                        visName.text = name
                        visPhone.text = phone

                        if(visCheckin.text == ""){
                            visCheckin.text = "Visitor haven't check-in yet"
                            visCheckin.setTextColor(Color.RED)
                        }

                        Linkify.addLinks(visPhone, Linkify.PHONE_NUMBERS)

                        Toast.makeText(requireContext(), "Plate: $plate", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error, data retrieve from fireStore failed", Toast.LENGTH_SHORT).show()
            }



        }else{
            visName.text = "not found"
        }

        visDelete.setOnClickListener {

            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Confimation")
                .setPositiveButton("Yes, Delete") { dialog, which ->

                    selectedVisitor?.let { visitorId ->
                        val docRef = fStore.collection("visitor").document(visitorId)

                        docRef.delete()
                            .addOnSuccessListener {
                                // Document deleted successfully
                                Toast.makeText(requireContext(), "Document deleted", Toast.LENGTH_SHORT).show()
                                val transaction = activity?.supportFragmentManager?.beginTransaction()
                                transaction?.replace(R.id.fragmentContainer, VisitorListFragment())
                                transaction?.addToBackStack(null)
                                transaction?.commit()
                            }
                            .addOnFailureListener { exception ->
                                // Deletion failed
                                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                            }
                    } ?: run {
                        // Handle case where selectedVisitor is null
                        Toast.makeText(requireContext(), "No visitor selected", Toast.LENGTH_SHORT).show()
                    }


                }
                .setNegativeButton("Cancel", null)
                .create()
            dialog.show()

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