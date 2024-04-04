package com.example.carlosfishingsuppliesims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var allProductsTextView: TextView
    private lateinit var lowProductsTextView: TextView
    private lateinit var outOfStockTextView: TextView
    private lateinit var allSalesTextView: TextView
    private lateinit var productsRef: DatabaseReference
    private lateinit var salesRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        allProductsTextView = view.findViewById(R.id.allProducts)
        lowProductsTextView = view.findViewById(R.id.lowProducts)
        outOfStockTextView = view.findViewById(R.id.allNoStock)
        allSalesTextView = view.findViewById(R.id.allSales)

        productsRef = FirebaseDatabase.getInstance().getReference("products")
        salesRef = FirebaseDatabase.getInstance().getReference("sales")

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
                    if (quantity in 1 until 5) {
                        lowProductsCount++
                    }
                }

                // Update the lowProductsTextView
                lowProductsTextView.text = lowProductsCount.toString()

                // Count products with zero quantity
                var outOfStockCount = 0
                for (productSnapshot in dataSnapshot.children) {
                    val quantity = productSnapshot.child("quantity").getValue(Long::class.java) ?: 0
                    if (quantity.toInt() == 0) {
                        outOfStockCount++
                    }
                }

                // Update the outOfStockTextView
                outOfStockTextView.text = outOfStockCount.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })

        // Listen for changes in sales and update the allSalesTextView
        salesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the count of all sales
                val numberOfSales = dataSnapshot.childrenCount

                // Update the allSalesTextView
                allSalesTextView.text = numberOfSales.toString()
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

        // Navigate to ProductsFragment when outOfStockNotification CardView is clicked
        val outOfStockNotificationCardView = view.findViewById<CardView>(R.id.outOfStockNotification)
        outOfStockNotificationCardView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_productsFragment)
        }

        // Navigate to SalesFragment when salesNotification CardView is clicked
        val salesNotificationCardView = view.findViewById<CardView>(R.id.salesNotification)
        salesNotificationCardView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_salesFragment)
        }

        return view
    }
}
