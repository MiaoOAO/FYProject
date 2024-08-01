package com.example.fyproject.admin

import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.fyproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AdminParkingReservationFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private var visitDateUpdate:String = ""
    private var plateNoUpdate:String = ""
    private var totalParking:Int = 50       //remember to change can modify by admin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_parking_reservation, container, false)

        db = FirebaseFirestore.getInstance()

        val pResName = view.findViewById<TextView>(R.id.parkingResNameAdmin)
        val pResVisDate = view.findViewById<TextView>(R.id.parkingResVisitDateAdmin)
        val pResPhone = view.findViewById<TextView>(R.id.parkingResPhoneAdmin)
        val pResLot = view.findViewById<TextView>(R.id.parkingResLotAdmin)
        val pResButton = view.findViewById<Button>(R.id.parkingReserveBtnAdmin)

        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        val query = db.collection("visitor").whereEqualTo("parkingStatus", "")

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val plateNumbers = mutableListOf<String>() // Make it mutable
                plateNumbers.add("Select here")
                plateNumbers.addAll(task.result.documents.map { it.getString("plateNo")!! })
                // create an adapter for the spinner
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, plateNumbers)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // set the adapter to the spinner
                val spinner = view.findViewById<Spinner>(R.id.plate_number_spinnerAdmin)
                spinner.adapter = adapter

                // handle spinner selection
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        val selectedPlateNumber = plateNumbers[position]

                        val docRef = db.collection("visitor").whereEqualTo("plateNo", selectedPlateNumber)
                        plateNoUpdate = selectedPlateNumber

                        docRef.get().addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot.documents) {
                                val data = document.data

                                if (data != null) {
                                    val name = data["name"] as String
                                    val visitDate = data["VisitDate"] as String
                                    val phone = data["phone"] as String

                                    // Update UI elements with retrieved data
                                    pResName.text = name
                                    pResVisDate.text = visitDate
                                    pResPhone.text = phone


                                    //This part is use for count the parking lot available
                                    val query = db.collection("visitor").whereEqualTo("parkingReserveDate", visitDate)
                                    visitDateUpdate = visitDate

                                    query.get().addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            var totalParkingNow = totalParking
                                            val documents = task.result.documents
                                            var count = 0
                                            count = documents.size

                                            totalParkingNow = totalParkingNow - count

                                            pResLot.text = totalParkingNow.toString()
                                        } else {
                                            // Handle error
                                        }
                                    }

                                    Linkify.addLinks(pResPhone, Linkify.PHONE_NUMBERS)
                                }


                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(requireContext(), "Error, data retrieve from fireStore failed", Toast.LENGTH_SHORT).show()
                        }

                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // do something when nothing is selected
                    }
                }
            } else {
                // handle error
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        }

        pResButton.setOnClickListener{

            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Parking Reservation Confirmation")
                .setMessage("Visitor Name : ${pResName.text} \nVisitor Plate : ${plateNoUpdate} \nVisitor Phone : ${pResPhone.text} \nParking Reserve Date : ${pResVisDate.text}")
                .setPositiveButton("Submit") { dialog, which ->

                    // Get the current document reference (replace with your document reference)
                    val query = db.collection("visitor").whereEqualTo("plateNo", plateNoUpdate)

                    query.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result.documents) {
                                val documentReference = document.reference
                                val data = HashMap<String, Any>()
                                data["parkingReserveDate"] = visitDateUpdate
                                data["parkingStatus"] = "1"


                                documentReference.update(data)
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(),"Updated sucessfully : $visitDateUpdate", Toast.LENGTH_SHORT).show()

                                        val query = db.collection("visitor").whereEqualTo("parkingReserveDate", visitDateUpdate)

                                        query.get().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                var totalParkingNow = totalParking
                                                val documents = task.result.documents
                                                var count = 0
                                                count = documents.size

                                                totalParkingNow = totalParkingNow - count

                                                pResLot.text = totalParkingNow.toString()
                                            } else {
                                                // Handle error
                                            }
                                        }

                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(requireContext(),"Failed to retrieve documents", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                .setNegativeButton("Cancel", null)
                .create()
            dialog.show()






        }


        return view
    }


}