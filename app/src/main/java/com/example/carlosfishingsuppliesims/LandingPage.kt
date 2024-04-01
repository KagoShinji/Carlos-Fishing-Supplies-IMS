package com.example.carlosfishingsuppliesims

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
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

        // Initialize NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true

            when (menuItem.itemId) {
                R.id.nav_home -> navController.navigate(R.id.homeFragment)
                R.id.nav_sales -> navController.navigate(R.id.salesFragment)
                R.id.nav_products -> navController.navigate(R.id.productsFragment)
                R.id.nav_logout -> logoutAndStartLoadingScreen()
            }

            drawerLayout.closeDrawers()
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

    private fun logoutAndStartLoadingScreen() {
        // Implement logout logic
        // For example, if you're using Firebase Auth, you can call FirebaseAuth.getInstance().signOut()
        // After logging out, start the loading screen activity
        startActivity(Intent(this, Splash::class.java))

        // Close all sessions and finish the current activity
        finish()
    }
}
