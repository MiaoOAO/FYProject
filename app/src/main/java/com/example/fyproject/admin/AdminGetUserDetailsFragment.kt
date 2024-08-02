package com.example.fyproject.admin

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

class AdminGetUserDetailsFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_get_user_details, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val userName = view.findViewById<TextView>(R.id.userDetailsNameUserAdmin)
        val userPlate = view.findViewById<TextView>(R.id.userDetailsPlateUserAdmin)
        val userAddress = view.findViewById<TextView>(R.id.userAddressUserAdmin)
        val userIcNo = view.findViewById<TextView>(R.id.userDetailsIcNoUserAdmin)
        val userPhone = view.findViewById<TextView>(R.id.userDetailsPhoneUserAdmin)
        val userDelete = view.findViewById<Button>(R.id.userDetailsDelButtonUserAdmin)
        val selectedUser = arguments?.getString("user_id")

        if (selectedUser != null) {
            val docRef = fStore.collection("user").whereEqualTo("userId", selectedUser)
            docRef.get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val data = document.data

                    if (data != null) {
                        val plate = data["plateNo"] as String
                        val address = data["address"] as String
                        val icNo = data["icNo"] as String
                        val name = data["name"] as String
                        val phone = data["phone"] as String
//                        val status = data["parkingStatus"] as String

                        // Update UI elements with retrieved data
                        userPlate.text = plate
                        userAddress.text = address
                        userIcNo.text = icNo
                        userName.text = name
                        userPhone.text = phone

//                        if (status == "1") {
//                            parkingResDelete.visibility = View.VISIBLE
//                        } else {
//                            parkingResDelete.visibility = View.INVISIBLE
//                        }


//                        if(visCheckin.text == ""){
//                            visCheckin.text = "Visitor haven't check-in yet"
//                            visCheckin.setTextColor(Color.RED)
//                        }

                        Linkify.addLinks(userPhone, Linkify.PHONE_NUMBERS)

                        Toast.makeText(requireContext(), "Plate: $plate", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error, data retrieve from fireStore failed", Toast.LENGTH_SHORT).show()
            }



        }else{
            userName.text = "not found"
        }

        userDelete.setOnClickListener {

            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Confimation")
                .setPositiveButton("Yes, Delete") { dialog, which ->

                    selectedUser?.let { userId ->
                        val docRef = fStore.collection("visitor").document(userId)

                        docRef.delete()
                            .addOnSuccessListener {
                                // Document deleted successfully
                                Toast.makeText(requireContext(), "Document deleted", Toast.LENGTH_SHORT).show()
                                val transaction = activity?.supportFragmentManager?.beginTransaction()
                                transaction?.replace(R.id.adminFragmentContainer, AdminGetUserListFragment())
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