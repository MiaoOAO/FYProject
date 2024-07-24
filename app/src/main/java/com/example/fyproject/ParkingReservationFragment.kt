package com.example.fyproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ParkingReservationFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_parking_reservation, container, false)

        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        val query = db.collection("visitor").whereEqualTo("ownerId", userId)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val plateNumbers = task.result.documents.map { it.getString("plateNo")!! }
                // create an adapter for the spinner
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, plateNumbers)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // set the adapter to the spinner
                val spinner = view.findViewById<Spinner>(R.id.plate_number_spinner)
                spinner.adapter = adapter

                // handle spinner selection
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        val selectedPlateNumber = plateNumbers[position]
                        // do something with the selected plate number
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


        return view
    }

}