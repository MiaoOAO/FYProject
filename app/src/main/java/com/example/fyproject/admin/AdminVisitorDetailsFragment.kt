package com.example.fyproject.admin

import android.graphics.Color
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.fyproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AdminVisitorDetailsFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_visitor_details, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val visName = view.findViewById<TextView>(R.id.visDetailsNameAdmin)
        val visPlate = view.findViewById<TextView>(R.id.visDetailsPlateAdmin)
        val visCheckin = view.findViewById<TextView>(R.id.visCheckinAdmin)
        val visDate = view.findViewById<TextView>(R.id.visDetailsVisitDateAdmin)
        val visPhone = view.findViewById<TextView>(R.id.visDetailsPhoneAdmin)
        val visDelete = view.findViewById<Button>(R.id.visDetailsDelButtonAdmin)
        val parkingResDelete = view.findViewById<Button>(R.id.parkingResCancelBtnAdmin)
        val visResident = view.findViewById<TextView>(R.id.visDetailsResidentAdmin)


        val selectedVisitor = arguments?.getString("visitor_id")

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
                        val status = data["parkingStatus"] as String
                        val owner = data["ownerId"] as String

                        // Update UI elements with retrieved data
                        visPlate.text = plate
                        visDate.text = visitDate
                        visCheckin.text = checkin
                        visName.text = name
                        visPhone.text = phone
                        visResident.text = owner

                        if (status == "1") {
                            parkingResDelete.visibility = View.VISIBLE
                        } else {
                            parkingResDelete.visibility = View.INVISIBLE
                        }


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
                                transaction?.replace(R.id.adminFragmentContainer, AdminVisitorListFragment())
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

        parkingResDelete.setOnClickListener{
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Confimation")
                .setPositiveButton("Yes, Delete") { dialog, which ->

                    selectedVisitor?.let { visitorId ->
                        val docRef = fStore.collection("visitor").document(visitorId)

                        val updates = hashMapOf<String, Any>(
                            "parkingReserveDate" to "",
                            "parkingStatus" to ""
                        )

                        docRef.update(updates)
                            .addOnSuccessListener {
                                // Document deleted successfully
                                Toast.makeText(requireContext(), "Parking Reservation cancelled", Toast.LENGTH_SHORT).show()
                                val transaction = activity?.supportFragmentManager?.beginTransaction()
                                transaction?.replace(R.id.adminFragmentContainer, AdminParkingListFragment())
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


}