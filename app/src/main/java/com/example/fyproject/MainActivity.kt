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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
//    private lateinit var userList: ArrayList<User>
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

                    if(getIc.isNotEmpty() || getName.isNotEmpty() || getPlateNo.isNotEmpty()){
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


}