package com.example.fyproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fyproject.databinding.ActivityProfileFormBinding
import com.example.fyproject.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFormActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityProfileFormBinding
    private lateinit var fStore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        binding.profileFormBtn.setOnClickListener{
            val icNo = binding.icNoReg.text.toString()
            val fName = binding.nameReg.text.toString()
            val hAddress = binding.addressReg.text.toString()
            val plateNo = binding.plateNoReg.text.toString()

            val updateUserMap = hashMapOf(
                "icNo" to icNo,
                "name" to fName,
                "address" to hAddress,
                "plateNo" to plateNo
            )

            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            if(icNo.isNotEmpty() && fName.isNotEmpty() && plateNo.isNotEmpty() && hAddress.isNotEmpty()){

                val intent = Intent(this, UserMainPage::class.java)

                fStore.collection("user").document(userId)
                                .update(updateUserMap as Map<String, Any>)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                                 this,
                                                 "Profile updated",
                                                 Toast.LENGTH_SHORT
                                             ).show()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(
                                        this,
                                        "Profile update failed $userId",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                startActivity(intent)

            }else{
                Toast.makeText(this, "Empty Fields is not allowed", Toast.LENGTH_SHORT).show()
            }
        }


        }
    }
