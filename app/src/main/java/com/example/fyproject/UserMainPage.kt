package com.example.fyproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.fyproject.databinding.ActivityMainBinding
import com.example.fyproject.databinding.ActivityUserMainPageBinding
import com.example.fyproject.listener.UserMainPageListener
import com.google.android.material.navigation.NavigationView


class UserMainPage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, UserMainPageListener{

    private lateinit var binding: ActivityUserMainPageBinding
    private lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            navView.bringToFront()

            setSupportActionBar(toolbar)

            toggle = ActionBarDrawerToggle(
                this@UserMainPage, myDrawerLayout, toolbar, R.string.nav_open, R.string.nav_close
            )
            toggle.isDrawerIndicatorEnabled = true
            myDrawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            navView.setNavigationItemSelectedListener(this@UserMainPage)

            setToolbarTitle("Home")

            if(savedInstanceState == null){
                supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, UserMainPageFragment()).commit()
                navView.setCheckedItem(R.id.home_nav_menu)
            }

        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.home_nav_menu -> {
                setToolbarTitle("Home")
                changeFragment(UserMainPageFragment())
            }

            R.id.visitorList_nav_menu -> {
                setToolbarTitle("Visitor List")
                changeFragment(VisitorListFragment())
            }

            R.id.profile_nav_menu-> {
                setToolbarTitle("Profile")
                changeFragment(ProfileFragment())
            }
        }

        binding.myDrawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

//    override fun onBackPressed() {
//        if(binding.myDrawerLayout.isDrawerOpen(GravityCompat.START)){
//            binding.myDrawerLayout.closeDrawer(GravityCompat.START)
//        }else{
//            onBackPressedDispatcher.onBackPressed()
//        }
//
//    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            // No back stack entries, navigate to home fragment
            setToolbarTitle("Home")
            val fragment = supportFragmentManager.beginTransaction()
            fragment.replace(R.id.fragmentContainer, UserMainPageFragment()).commit()
        } else {
            super.onBackPressed()// Let default back stack handling occur
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

    fun setToolbarTitle(title:String){
        supportActionBar?.title = title
    }

    fun changeFragment(frag: Fragment){
        val fragment = supportFragmentManager.beginTransaction()
        fragment.replace(R.id.fragmentContainer, frag).commit()
    }

    override fun onFragmentAction(data: String) {
        setToolbarTitle(data)
    }


}



