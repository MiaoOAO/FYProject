package com.example.fyproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import com.example.fyproject.databinding.ActivityMainBinding
import com.example.fyproject.databinding.ActivityUserMainPageBinding
import com.google.android.material.navigation.NavigationView


class UserMainPage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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
                this@UserMainPage,
                myDrawerLayout,
                toolbar,
                R.string.nav_open,
                R.string.nav_close
            )
            toggle.isDrawerIndicatorEnabled = true
            myDrawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            navView.setNavigationItemSelectedListener(this@UserMainPage)

            setToolbarTitle("Home")

        }

//                when(it.itemId){
//                    R.id.home_nav_menu -> {
//                        setToolbarTitle("Home")
//                        changeFragment(UserMainPage())
//                        Toast.makeText(this@UserMainPage,"Home is selected", Toast.LENGTH_SHORT).show()
//                        myDrawerLayout.closeDrawer(GravityCompat.START)
//
//                        return true
//                    }
//
//                    R.id.visitorList_nav_menu -> {
//                        setToolbarTitle("Visitor List")
//                        Toast.makeText(this@UserMainPage,"visitor List is selected", Toast.LENGTH_SHORT).show()
//
//                        val Fragment = VisitorListFragment()
//                        val fragmentTransaction = supportFragmentManager.beginTransaction()
//                        fragmentTransaction.replace(R.id.visitorListLayout, Fragment) // Replace with replace if needed
//                        fragmentTransaction.commit()
//
//                    }
//
//                    R.id.profile_nav_menu -> {
//                        Toast.makeText(this@UserMainPage,"Profile is selected", Toast.LENGTH_SHORT).show()
//                    }
//
//
//                }
//                true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.home_nav_menu -> {
                setToolbarTitle("Home")
                var intent = Intent(this, UserMainPage::class.java)
                startActivity(intent);
            }

            R.id.visitorList_nav_menu -> {
                setToolbarTitle("Visitor List")
                changeFragment(VisitorListFragment())
            }
        }

        return true
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

}



