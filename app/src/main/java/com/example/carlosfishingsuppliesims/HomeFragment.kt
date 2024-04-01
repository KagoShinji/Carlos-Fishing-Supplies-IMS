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
    private lateinit var lowProductsTextView: TextView
    private lateinit var productsRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        allProductsTextView = view.findViewById(R.id.allProducts)
        lowProductsTextView = view.findViewById(R.id.lowProducts)
        productsRef = FirebaseDatabase.getInstance().getReference("products")

        // Listen for changes in the database and update the TextViews accordingly
        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the count of all products
                val numberOfProducts = dataSnapshot.childrenCount

                // Update the allProductsTextView
                allProductsTextView.text = numberOfProducts.toString()

                // Count products with less than 5 quantity
                var lowProductsCount = 0
                for (productSnapshot in dataSnapshot.children) {
                    val quantity = productSnapshot.child("quantity").getValue(Long::class.java) ?: 0
                    if (quantity < 5) {
                        lowProductsCount++
                    }
                }

                // Update the lowProductsTextView
                lowProductsTextView.text = lowProductsCount.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })

        // Navigate to ProductsFragment when lowStockNotification CardView is clicked
        val lowStockNotificationCardView = view.findViewById<CardView>(R.id.lowStockNotification)
        lowStockNotificationCardView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_productsFragment)
        }

        // Navigate to ProductsFragment when productsNotification CardView is clicked
        val productsNotificationCardView = view.findViewById<CardView>(R.id.productsNotification)
        productsNotificationCardView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_productsFragment)
        }

        return view
    }
}
