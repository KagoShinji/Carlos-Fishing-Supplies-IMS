package com.example.carlosfishingsuppliesims

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class LandingPage : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val menuImageView: ImageView = findViewById(R.id.menu)
        val userProfileIcon: ImageView = findViewById(R.id.userProfileIcon) // Added

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Replace the FrameLayout with the fragment_home initially
        replaceFragment(HomeFragment(), "Home")

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true

            when (menuItem.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment(), menuItem.title.toString())
                R.id.nav_sales -> replaceFragment(SalesFragment(), menuItem.title.toString())
                R.id.nav_products -> replaceFragment(ProductsFragment(), menuItem.title.toString())
                R.id.nav_quantity -> replaceFragment(QuantityFragment(), menuItem.title.toString())
                R.id.nav_logout -> logout()
            }

            true
        }

        // Set OnClickListener to the menu ImageView to open the drawer when clicked
        menuImageView.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }

        // Set OnClickListener to the user profile icon for handling profile click
        userProfileIcon.setOnClickListener {
            // Handle click event for user profile icon
            // For example, open profile activity
            // Here, I'm showing a simple Toast message for demonstration
            Toast.makeText(this, "User profile clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        TODO("Not yet implemented")
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)
    }
}
