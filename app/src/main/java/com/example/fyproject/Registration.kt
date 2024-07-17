package com.example.fyproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fyproject.databinding.ActivityRegistrationBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class Registration : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var fStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        binding.signUpBtn.setOnClickListener{
            val email = binding.emailReg.text.toString()
            val pass = binding.passwordReg.text.toString()
            val confirmPass = binding.passwordconfirmReg.text.toString()
            //val fullName = binding.nameReg.text.toString()
            val phone = binding.phoneReg.text.toString()

            val userMap = hashMapOf(
                "email" to email,
                "password" to pass,
                "phone" to phone,
                "icNo" to "",
                "name" to "",
                "address" to "",
                "plateNo" to ""
            )

//            val userId = FirebaseAuth.getInstance().currentUser!!.uid


            if(email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()){
                if(pass == confirmPass){
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)

                                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                                //connent and store the user data to firebase firestore
                                fStore.collection("user").document(userId).set(userMap).addOnSuccessListener {
                                    Toast.makeText(this, "Account register successful", Toast.LENGTH_SHORT).show()
                                }
                                    .addOnFailureListener{
                                    Toast.makeText(this, "Failed to added", Toast.LENGTH_SHORT).show()
                                    }

                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Password is not matched", Toast.LENGTH_SHORT).show()
                }
            }else{
                    Toast.makeText(this, "Empty Fields is not allowed", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

