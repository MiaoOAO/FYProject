package com.example.fyproject.admin

import android.app.DatePickerDialog
import android.os.Bundle
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
import java.util.Calendar

class AdminVisitorRegistrationFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var datePickerDialog: DatePickerDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_visitor_registration, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val vBtn: Button = view.findViewById(R.id.visSubmitBtnAdmin)

        //Date Picker by using dialog method
        val dateLabel = view.findViewById<TextView>(R.id.visDateTvAdmin)
        var selectedDate: String? = null
        val calendar = Calendar.getInstance()
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        val today = Calendar.getInstance() // Get today's date

        datePickerDialog = DatePickerDialog(requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                // Handle selected date (update label, perform actions)
                selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"

                dateLabel?.text = "Selected Date: $selectedDate"
            },
            initialYear, initialMonth, initialDay
        )

        datePickerDialog.datePicker.minDate = today.timeInMillis

        // (Optional) Set onClick listener on a button to show the dialog
        val selectDateButton = view.findViewById<Button>(R.id.visDateBtnAdmin)
        selectDateButton.setOnClickListener {
            datePickerDialog.show()
        }


//      add visitor registration to firestore
        vBtn.setOnClickListener{
            val vName = view.findViewById<TextView?>(R.id.visRegNameAdmin).text.toString()
            val vPlate = view.findViewById<TextView?>(R.id.visPlateNoAdmin).text.toString().uppercase()
            val vPhone = view.findViewById<TextView?>(R.id.visPhoneAdmin).text.toString()

            if(vName != "" && vPlate != "" && vPhone != "" && selectedDate != "") {

                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("Confimation")
                    .setMessage("Visitor Name : $vName \nVisitor Plate : $vPlate \nVisitor Phone : $vPhone \nVisit Date : $selectedDate")
                    .setPositiveButton("Submit") { dialog, which ->
                        val userId = FirebaseAuth.getInstance().currentUser!!.uid

                        val visitorMap = hashMapOf(
                            "name" to vName,
                            "plateNo" to vPlate,
                            "phone" to vPhone,
                            "VisitDate" to selectedDate,
                            "status" to 0,
                            "checkInDate" to "",
                            "checkoutDate" to "",
                            "parkingReserveDate" to "",
                            "ownerId" to userId,
                            "parkingStatus" to ""
                        )


                        fStore.collection("visitor")
                            .add(visitorMap)
                            .addOnSuccessListener { documentReference ->
                                val visitorId = documentReference.id
                                val updatedVisitorMap = visitorMap.plus("visitorId" to visitorId)
                                documentReference.set(updatedVisitorMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            requireContext(),
                                            "Visitor registered",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val transaction = activity?.supportFragmentManager?.beginTransaction()
                                        transaction?.replace(R.id.adminFragmentContainer, AdminMainPageFragment())
                                        transaction?.addToBackStack(null)
                                        transaction?.commit()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            requireContext(),
                                            "Visitor register failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()
                            }

                    }
                    .setNegativeButton("Cancel", null)
                    .create()
                dialog.show()

            }else{
                Toast.makeText(requireContext(), "text field cannot be blank", Toast.LENGTH_SHORT).show()
            }

        }

        return view
    }



}