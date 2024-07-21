package com.example.fyproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.fyproject.admin.AdminLoginActivity
import com.example.fyproject.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var fireStoreDb = Firebase.firestore

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
        val resetPass: TextView = findViewById(R.id.resetPassLogin)
        val adminLogin: TextView = findViewById(R.id.adminLogin)

        adminLogin.setOnClickListener{
            val intent = Intent(this, AdminLoginActivity::class.java)
          startActivity(intent)
        }

        resetPass.setOnClickListener{
            showResetPasswordDialog()
        }


        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        login.setOnClickListener{
            val email = binding.emailLogin.text.toString()
            val pass = binding.passLogin.text.toString()


            if(email.isNotEmpty() && pass.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            checkUserProfileComplete()
//                            val intent = Intent(this, UserMainPage::class.java)
//                            startActivity(intent)
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


    }

    private fun checkUserProfileComplete() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = fireStoreDb.collection("user").document(userId)

        ref.get().addOnSuccessListener {
            if(it != null){
                val getIc = it.data?.get("icNo").toString()
                val getName = it.data?.get("name").toString()
                val getPlateNo = it.data?.get("plateNo").toString()
                val getAddress = it.data?.get("address").toString()

                    if(getIc.isNotEmpty() || getName.isNotEmpty() || getPlateNo.isNotEmpty() || getAddress.isNotEmpty()){
                    Toast.makeText(this, "Success to get Email:$getIc, name:$getName, phone:$getPlateNo", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UserMainPage::class.java)
                    startActivity(intent)
                    }else{
                        Toast.makeText(this, "Please Fill in the required information userID: $userId", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ProfileFormActivity::class.java)
                        startActivity(intent)
                    }

            }
        }
            .addOnFailureListener{
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
            }

    }

    //Reset password function
    private fun showResetPasswordDialog() {
        val emailInput = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setMessage("Enter your email address to receive a reset link in your email inbox.")
            .setView(emailInput)
            .setPositiveButton("Reset") { dialog, which ->
                val emailReset = emailInput.text.toString().trim()
                sendResetPasswordEmail(emailReset)
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun sendResetPasswordEmail(emailReset: String) {

        val accountEmailRef = fireStoreDb.collection("user")

    if(emailReset.isNotEmpty()) {

        val query = if (emailReset.isNotEmpty()) {
            accountEmailRef.whereEqualTo("email", emailReset)  // Filter by email entered
        } else {
            accountEmailRef  // No filter, retrieve all documents
        }

// Perform the query and handle results
        query.get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Email not found, please enter again", Toast.LENGTH_SHORT).show()
                } else {
                    val emails = mutableListOf<String>()
                    for (document in documents) {
                        val emailSearch = document.getString("email")
                        if (emailSearch != null) {
                            emails.add(emailSearch)
                        }
                    }


                    firebaseAuth.sendPasswordResetEmail(emailReset)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Email sent for password reset!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Failed to send reset email!", Toast.LENGTH_SHORT).show()
                            }
                        }

                    Toast.makeText(this, "Reset link sent to your email inbox, please check", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "System error, failed", Toast.LENGTH_SHORT).show()
            }
    }else{
        Toast.makeText(this, "Input cannot be blank", Toast.LENGTH_SHORT).show()
    }


    }


}