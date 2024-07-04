package com.example.fyproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val signUp: Button = findViewById(R.id.signUp)

        signUp.setOnClickListener{
            var intent = Intent(this, Registration::class.java)
            startActivity(intent);
        }
    }
}