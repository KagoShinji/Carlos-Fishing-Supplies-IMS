package com.example.carlosfishingsuppliesims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var allProductsTextView: TextView
    private lateinit var productsRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        allProductsTextView = view.findViewById(R.id.allProducts)
        productsRef = FirebaseDatabase.getInstance().getReference("products")

        // Listen for changes in the database and update the TextView accordingly
        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the count of products
                val numberOfProducts = dataSnapshot.childrenCount

                // Update the TextView
                allProductsTextView.text = numberOfProducts.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })

        val cardView = view.findViewById<CardView>(R.id.productsNotification)
        cardView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_productsFragment)
        }

        return view
    }
}
