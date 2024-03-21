package com.example.carlosfishingsuppliesims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.carlosfishingsuppliesims.adapter.SalesAdapter
import com.example.carlosfishingsuppliesims.models.Product
import com.example.carlosfishingsuppliesims.models.Sales

class SalesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var salesAdapter: SalesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sales, container, false)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize Firebase Realtime Database
        database = Firebase.database.reference.child("sales")

        // Initialize RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewSales)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Use requireContext() to get the Fragment's context
        salesAdapter = SalesAdapter(ArrayList())
        recyclerView.adapter = salesAdapter

        // Fetch data from Firebase
        fetchDataFromFirebase()

        return view
    }

    private fun fetchDataFromFirebase() {
        val salesList: MutableList<Sales> = mutableListOf()
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    // Retrieve sales key
                    val key = dataSnapshot.key

                    val dateTime = dataSnapshot.child("dateTime").getValue(String::class.java)
                    val totalPrice = dataSnapshot.child("totalPrice").getValue(Double::class.java)

                    val productsList = mutableListOf<Product>()
                    for (productSnapshot in dataSnapshot.child("products").children) {
                        val productName = productSnapshot.child("name").getValue(String::class.java)
                        val quantity = productSnapshot.child("quantity").getValue(Int::class.java)
                        val price = productSnapshot.child("unitPrice").getValue(Double::class.java)

                        // Adjust the creation of Product instances
                        val product = Product(
                            "", // Set an empty string for the key or provide a valid key
                            productName ?: "", // Set the name or provide a default value
                            null, // Set null or provide a value for description if needed
                            quantity ?: 0, // Set the quantity or provide a default value
                            price?.toString() ?: "", // Convert price to string and set it as unitPrice or provide a default value
                            0L // Set timestamp to 0 or provide a valid timestamp if needed
                        )
                        productsList.add(product)
                    }

                    if (key != null && dateTime != null && totalPrice != null) {
                        val sales = Sales(key, dateTime, totalPrice, productsList) // Pass key to Sales constructor
                        salesList.add(sales)
                    }
                }
                salesAdapter.updateData(salesList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = SalesFragment()
    }
}
