package com.example.carlosfishingsuppliesims

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carlosfishingsuppliesims.R
import com.example.carlosfishingsuppliesims.adapter.MyAdapter
import com.example.carlosfishingsuppliesims.models.Product
import com.google.android.material.snackbar.Snackbar
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
        val view = inflater.inflate(R.layout.fragment_products, container, false)

        // Initialize RecyclerView and set its layout manager
        productRecyclerView = view.findViewById(R.id.recyclerView)
        productRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter with click listeners for edit and delete
        adapter = MyAdapter(
            editClickListener = { position -> onEditProduct(position) },
            deleteClickListener = { productKey -> onDeleteProduct(productKey) }
        )
        productRecyclerView.adapter = adapter // Set adapter to RecyclerView

        // Fetch products from Firebase Realtime Database
        fetchProducts()

        return view
    }

    private fun fetchProducts() {
        // Show loading spinner
        // You can use a ProgressBar or a Shimmer effect to indicate loading

        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productsList = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val key = productSnapshot.key?.toInt() ?: 0
                    val name = productSnapshot.child("name").getValue(String::class.java) ?: ""
                    val description = productSnapshot.child("description").getValue(String::class.java) ?: ""
                    val quantity = productSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                    val unitPrice = productSnapshot.child("unitPrice").getValue(String::class.java) ?: "0.0"
                    val product = Product(key, name, description, quantity, unitPrice)
                    productsList.add(product)
                }
                adapter.updateProductList(productsList)

                // Hide loading spinner
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                // Hide loading spinner and show error message
                showMessage("Failed to fetch products. ${error.message}")
            }
        })
    }

    private fun onEditProduct(position: Int) {
        // Implement logic for editing product
        // You might navigate to another fragment/activity to perform editing
        // You can pass the product details to the editing screen using Intent or ViewModel
        // Example:
        // val product = adapter.getProduct(position)
        // val intent = Intent(requireContext(), EditProductActivity::class.java)
        // intent.putExtra("product", product)
        // startActivity(intent)
    }

    private fun onDeleteProduct(productKey: String) {
        // Confirmation dialog before deleting the product
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Delete") { dialog, which ->
                // Delete product from Firebase Realtime Database
                productsRef.child(productKey).removeValue()
                    .addOnSuccessListener {
                        // Display successful deletion message
                        showMessage("Product deleted successfully.")
                    }
                    .addOnFailureListener { exception ->
                        // Display deletion failure message
                        showMessage("Failed to delete product. Please try again later.")
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showMessage(message: String) {
        // Show message using Snackbar or Toast
        // For simplicity, we'll use a Snackbar here
        val view = requireActivity().findViewById<View>(android.R.id.content)
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
}