package com.example.carlosfishingsuppliesims

import com.example.carlosfishingsuppliesims.adapter.MyAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carlosfishingsuppliesims.models.Product
import com.google.firebase.database.*

class ProductsFragment : Fragment() {

    private lateinit var productRecyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val productsRef: DatabaseReference = database.getReference("products")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_products, container, false)

        // Initialize RecyclerView and set its layout manager
        productRecyclerView = view.findViewById(R.id.recyclerView)
        productRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter with click listeners for edit and delete
        adapter = MyAdapter(
            editClickListener = { position -> onEditProduct(position) },
            deleteClickListener = { position -> onDeleteProduct(position) }
        )
        productRecyclerView.adapter = adapter // Set adapter to RecyclerView

        // Fetch products from Firebase Realtime Database
        fetchProducts()

        return view
    }

    // Function to fetch products from Firebase Realtime Database
    private fun fetchProducts() {
        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productsList = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val key = productSnapshot.key?.toInt() ?: 0 // Retrieve and convert key to Int
                    val name = productSnapshot.child("name").getValue(String::class.java) ?: ""
                    val description = productSnapshot.child("description").getValue(String::class.java) ?: ""
                    val quantity = productSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                    val unitPrice = productSnapshot.child("unitPrice").getValue(String::class.java) ?: "0.0" // Retrieve as String
                    val product = Product(key, name, description, quantity, unitPrice) // Include unitPrice as String
                    productsList.add(product)
                }
                adapter.updateProductList(productsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    // Function to handle edit product click
    private fun onEditProduct(position: Int) {
        // Handle edit product action here
    }

    // Function to handle delete product click
    private fun onDeleteProduct(position: Int) {
        // Handle delete product action here
    }
}
