package com.example.carlosfishingsuppliesims

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LandingPage : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var orangeNotification: ImageView
    lateinit var orangeQuantity: TextView
    lateinit var redNotification: ImageView
    lateinit var redQuantity: TextView
    lateinit var productsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val menuImageView: ImageView = findViewById(R.id.menu)
        orangeNotification = findViewById(R.id.orangeNotification)
        orangeQuantity = findViewById(R.id.orangeQuantity)
        redNotification = findViewById(R.id.redNotification)
        redQuantity = findViewById(R.id.redQuantity)

        // Initialize Firebase Database reference
        productsRef = FirebaseDatabase.getInstance().reference.child("products")

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

        // Set up ValueEventListener to update orange and red notifications
        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalOrangeStock = 0
                var totalRedStock = 0

                for (productSnapshot in dataSnapshot.children) {
                    val quantity = productSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                    if (quantity in 1 until 5) {
                        totalOrangeStock++
                    } else if (quantity == 0) {
                        totalRedStock++
                    }
                }

                // Update orange notification
                if (totalOrangeStock > 0) {
                    orangeNotification.visibility = View.VISIBLE
                    orangeQuantity.text = totalOrangeStock.toString()
                    orangeQuantity.visibility = View.VISIBLE
                } else {
                    orangeNotification.visibility = View.GONE
                    orangeQuantity.visibility = View.GONE
                }

                // Update red notification
                if (totalRedStock > 0) {
                    redNotification.visibility = View.VISIBLE
                    redQuantity.text = totalRedStock.toString()
                    redQuantity.visibility = View.VISIBLE
                } else {
                    redNotification.visibility = View.GONE
                    redQuantity.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })
    }

    private fun logoutAndStartLoadingScreen() {
        // Sign out the user from Firebase Auth
        FirebaseAuth.getInstance().signOut()

        // After logging out, start the loading screen activity
        startActivity(Intent(this, Splash::class.java))

        // Close all sessions and finish the current activity
        finish()
    }
}
