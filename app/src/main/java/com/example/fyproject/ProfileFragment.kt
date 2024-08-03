package com.example.fyproject

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

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
    private lateinit var storage: FirebaseStorage
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val pName:TextView = view.findViewById(R.id.nameTv)
        val pEmail:TextView = view.findViewById(R.id.emailTv)
        val pPhone:TextView = view.findViewById(R.id.phoneNoTv)
        val pAddress:TextView = view.findViewById(R.id.addressTv)
        val pPlateNo:TextView = view.findViewById(R.id.plateNoTv)
        val pIc:TextView = view.findViewById(R.id.icNoTv)
        val pImage:ImageView = view.findViewById(R.id.profilePic)


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
                    val imageUrl = data["profileImg"] as String?
                    val plateNo = data["plateNo"] as String?

                    val storageRef = storage.reference.child("images/$imageUrl")
                    val localFile = File.createTempFile("image", "jpg")

                    storageRef.getFile(localFile)
                        .addOnSuccessListener {
                            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                            pImage.setImageBitmap(bitmap)
                            Toast.makeText(requireContext(), "Profile image retrieved", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener{
                            Toast.makeText(requireContext(), "Profile image retrieve failed", Toast.LENGTH_SHORT).show()
                        }

                    // Update UI elements with retrieved data
                    pEmail.text = email
                    pName.text = name
                    pPhone.text = phone
                    pAddress.text = address
                    pIc.text = ic
                    pPlateNo.text = plateNo

                    Toast.makeText(requireContext(), "Email: $pEmail , Name: $pName", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { exception ->

            Toast.makeText(requireContext(), "Error, data retrieve from fireStore failed", Toast.LENGTH_SHORT).show()
        }


        return view


    }


}
