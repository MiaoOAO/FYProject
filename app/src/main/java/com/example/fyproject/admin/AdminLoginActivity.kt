package com.example.fyproject.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fyproject.R
import com.example.fyproject.databinding.ActivityAdminLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var fStore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        fStore = FirebaseFirestore.getInstance()

        val login: Button = findViewById(R.id.adminLoginBtn)

        login.setOnClickListener {
            val email = binding.adminEmailLogin.text.toString()
            val pass = binding.adminPassLogin.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {


                        val userId = FirebaseAuth.getInstance().currentUser!!.uid


                        val docRef = fStore.collection("admin").whereEqualTo("adminId", userId)

                        docRef.get().addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot.documents) {
                                val data = document.data

                                if (data != null) {
                                    val intent = Intent(this, AdminMainPage::class.java)
                                    startActivity(intent)
                                }
                                else{
                                    Toast.makeText(
                                        this,
                                        "Error, you are not an admin.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }.addOnFailureListener { exception ->

                            Toast.makeText(
                                this,
                                "Error, data retrieve from fireStore failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }else {
                        Toast.makeText(this, "Email or Password is invalid", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields is not allowed", Toast.LENGTH_SHORT).show()
            }
        }


    }
}