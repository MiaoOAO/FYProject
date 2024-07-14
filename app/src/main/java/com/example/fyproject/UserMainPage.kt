package com.example.fyproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.fyproject.databinding.ActivityMainBinding
import com.example.fyproject.databinding.ActivityUserMainPageBinding

class UserMainPage : AppCompatActivity() {

    private lateinit var binding: ActivityUserMainPageBinding
    private lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            navView.bringToFront()

            setSupportActionBar(toolbar)

            toggle = ActionBarDrawerToggle(this@UserMainPage, myDrawerLayout, R.string.nav_open, R.string.nav_close)

            myDrawerLayout.addDrawerListener(toggle)

            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.home_nav_menu -> {
                        Toast.makeText(this@UserMainPage,"Home is selected", Toast.LENGTH_SHORT).show()
                    }

                    R.id.profile_nav_menu -> {
                        Toast.makeText(this@UserMainPage,"Profile is selected", Toast.LENGTH_SHORT).show()
                    }


                }
                true
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // check conndition for drawer item with menu item
        return if (toggle.onOptionsItemSelected(item)){
            true
        }else{
            super.onOptionsItemSelected(item)
        }

    }
    }
