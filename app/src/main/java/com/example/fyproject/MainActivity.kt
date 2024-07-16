package com.example.fyproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.fyproject.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.auth.User

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val signUp: Button = findViewById(R.id.signUp)
        val login: Button= findViewById(R.id.userLogin)

        firebaseAuth = FirebaseAuth.getInstance()

        login.setOnClickListener{
            val email = binding.emailLogin.text.toString()
            val pass = binding.passLogin.text.toString()


            if(email.isNotEmpty() && pass.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
//                            checkUserProfileComplete()
                            val intent = Intent(this, UserMainPage::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Email or Password is invalid", Toast.LENGTH_SHORT).show()
                        }
                    }
            }else{
                Toast.makeText(this, "Empty Fields is not allowed", Toast.LENGTH_SHORT).show()
            }
        }

        signUp.setOnClickListener{
            var intent = Intent(this, Registration::class.java)
            startActivity(intent);
        }

        database = Firebase.database.reference

    }

//    private fun checkUserProfileComplete() {
//        val userId = firebaseAuth.currentUser?.uid ?: return // Get current user ID or return if null
//
//        database.child("users").child(userId).addListenerForSingleValueEvent(object :
//            ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    val user = snapshot.getValue(User::class.java) // Assuming a User class with relevant fields
//                    if (user?.icNo.isNullOrEmpty() || user?.fullName.isNullOrEmpty() || user?.carPlateNo.isNullOrEmpty()) {
//                        // Profile incomplete, navigate to profile completion fragment
//                        val intent = Intent(this@MainActivity, ProfileCompletionFragment::class.java)
//                        startActivity(intent)
//                    } else {
//                        // Profile complete, navigate to UserMainPage
//                        val intent = Intent(this@MainActivity, UserMainPage::class.java)
//                        startActivity(intent)
//                    }
//                } else {
//                    // Handle potential user data retrieval error (optional)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle database error (optional)
//                Toast.makeText(this@MainActivity, "Error checking profile data", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }


}