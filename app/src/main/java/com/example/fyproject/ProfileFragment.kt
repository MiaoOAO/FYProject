package com.example.fyproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.fyproject.databinding.ActivityProfileFormBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val pName:TextView = view.findViewById(R.id.nameTv)
        val pEmail:TextView = view.findViewById(R.id.emailTv)
        val pPhone:TextView = view.findViewById(R.id.phoneNoTv)
        val pAddress:TextView = view.findViewById(R.id.addressTv)
        val pIc:TextView = view.findViewById(R.id.icNoTv)


        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        val docRef = fStore.collection("user").document(userId)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val data = document.data

                if (data != null) {
                    val email = data["email"] as String
                    val name = data["name"] as String?
                    val phone = data["phone"] as String?
                    val address = data["address"] as String?
                    val ic = data["icNo"] as String?

                    // Update UI elements with retrieved data
                    pEmail.text = email
                    pName.text = name
                    pPhone.text = phone
                    pAddress.text = address
                    pIc.text = ic

                    Toast.makeText(requireContext(), "Email: $pEmail , Name: $pName", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { exception ->

            Toast.makeText(requireContext(), "Error, data retrieve from fireStore failed", Toast.LENGTH_SHORT).show()
        }


        return view


    }


}
