package com.example.fyproject

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.fyproject.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
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
    private lateinit var datePickerDialog: DatePickerDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_visitor_registration, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val vBtn:Button = view.findViewById(R.id.visSubmitBtn)
        val sBtn:Button = view.findViewById(R.id.searchVisBtn)


        //Date Picker by using dialog method
        val dateLabel = view.findViewById<TextView>(R.id.visDateTv)
        var selectedDate: String? = null
        val calendar = Calendar.getInstance()
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                // Handle selected date (update label, perform actions)
                selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"

                dateLabel?.text = "Selected Date: $selectedDate"
            },
            initialYear, initialMonth, initialDay
        )

        // (Optional) Set onClick listener on a button to show the dialog
        val selectDateButton = view.findViewById<Button>(R.id.visDateBtn)
        selectDateButton.setOnClickListener {
            datePickerDialog.show()
        }


//      add visitor registration to firestore
        vBtn.setOnClickListener{

            val vName = view.findViewById<TextView?>(R.id.visName).text.toString()
            val vPlate = view.findViewById<TextView?>(R.id.visPlateNo).text.toString()
            val vPhone = view.findViewById<TextView?>(R.id.visPhone).text.toString()
//            val vDate = view.findViewById<TextView?>(R.id.visDate).text.toString()

            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            val visitorMap = hashMapOf(
                "name" to vName,
                "plateNo" to vPlate,
                "phone" to vPhone,
                "VisitDate" to selectedDate,
                "status" to 0,
                "checkInDate" to "",
                "checkoutDate" to "",
                "ownerId" to userId
            )


            fStore.collection("visitor").document().set(visitorMap).addOnSuccessListener {
                Toast.makeText(requireContext(), "Visitor registered", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener{
                    Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT).show()
                }

        }



        sBtn.setOnClickListener{
            //search vehicle plate function
            checkPlateNumberDialog()
        }







        return view
    }

    private fun checkPlateNumberDialog() {
        val plateNoInput = EditText(requireContext())
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Check Car Plate Number")
            .setMessage("Enter Car Plate Number to check: ")
            .setView(plateNoInput)
            .setPositiveButton("Check") { dialog, which ->
                val plateNoCheck = plateNoInput.text.toString().trim()

                val visitorRef = fStore.collection("visitor")

                if (plateNoCheck.isNotEmpty()) {
                    val query = if (plateNoCheck.isNotEmpty()) {
                        visitorRef.whereEqualTo("plateNo", plateNoCheck)  // Filter by plateNo
                    } else {
                        visitorRef  // Keep filtering until the end, retrieve all documents
                    }

                    query.get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                Toast.makeText(
                                    requireContext(),
                                    "No plate number found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val plateNumbers = mutableListOf<String>()
                                for (document in documents) {
                                    val plateNo = document.getString("plateNo")
                                    if (plateNo != null) {
                                        plateNumbers.add(plateNo)
                                    }
                                }

                                Toast.makeText(
                                    requireContext(),
                                    "Plate Number: $plateNumbers found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT)
                                .show()
                        }

                }else{
                    Toast.makeText(requireContext(), "Input cannot be blank", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

}