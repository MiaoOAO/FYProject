package com.example.fyproject.admin

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.fyproject.R
import com.example.fyproject.databinding.ActivityAdminMainPageBinding
import com.google.android.material.navigation.NavigationView

class AdminMainPage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var binding: ActivityAdminMainPageBinding
    private lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            navView.bringToFront()

            setSupportActionBar(adminToolbar)

            toggle = ActionBarDrawerToggle(
                this@AdminMainPage, myAdminDrawerLayout, adminToolbar, R.string.nav_open, R.string.nav_close
            )

            toggle.isDrawerIndicatorEnabled = true
            myAdminDrawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            navView.setNavigationItemSelectedListener(this@AdminMainPage)

            setToolbarTitle("Home")

            if(savedInstanceState == null){
                supportFragmentManager.beginTransaction().replace(R.id.adminFragmentContainer, AdminMainPageFragment()).commit()
                navView.setCheckedItem(R.id.home_nav_menu)
            }

        }

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home_nav_menu -> {
                setToolbarTitle("Home")
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.adminFragmentContainer, AdminMainPageFragment()).commit()
            }

            R.id.visitorList_nav_menu -> {
                setToolbarTitle("Visitor List")
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.adminFragmentContainer, AdminVisitorListFragment()).commit()
            }

            R.id.profile_nav_menu-> {
                setToolbarTitle("Profile")
//                changeFragment(ProfileFragment())
            }

//            R.id.scan_nav_menu-> {
//                setToolbarTitle("Scanner")
//                changeFragment(ScanPlateFragment())
//                val intent = Intent(this, ScanPlateActivity::class.java)
//                startActivity(intent)
//            }

            R.id.logout_nav_menu-> {
                var intent = Intent(this, AdminLoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        binding.myAdminDrawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    fun setToolbarTitle(title:String){
        supportActionBar?.title = title
    }
//
//    override fun onBackPressed() {
//        if (supportFragmentManager.backStackEntryCount == 0) {
//
//            // No back stack entries, navigate to home fragment
////            val fragment = supportFragmentManager.beginTransaction()
////            fragment.replace(R.id.fragmentContainer, UserMainPageFragment()).commit()
//
//            setToolbarTitle("Home")
//
//        } else {
//            super.onBackPressed()// Let default back stack handling occur
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // check conndition for drawer item with menu item
//        return if (toggle.onOptionsItemSelected(item)){
//            true
//        }else{
//            super.onOptionsItemSelected(item)
//        }
//
//    }
//
//    override fun onFragmentAction(data: String) {
//        setToolbarTitle(data)
//    }
}