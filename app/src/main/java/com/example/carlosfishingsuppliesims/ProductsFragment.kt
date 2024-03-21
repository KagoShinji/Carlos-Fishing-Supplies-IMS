package com.example.carlosfishingsuppliesims

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carlosfishingsuppliesims.adapter.MyAdapter
import com.example.carlosfishingsuppliesims.models.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import java.util.*

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

        // Initialize the adapter
        adapter = MyAdapter()
        productRecyclerView.adapter = adapter // Set adapter to RecyclerView

        // Fetch products from Firebase Realtime Database
        fetchProducts()

        // Setup FAB for adding products
        val fabAddProduct: FloatingActionButton = view.findViewById(R.id.fab_add_product)
        fabAddProduct.setOnClickListener {
            showAddProductDialog()
        }

        return view
    }

    private fun fetchProducts() {
        // Show loading spinner
        // You can use a ProgressBar or a Shimmer effect to indicate loading

        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productsList = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val key = productSnapshot.key ?: ""
                    val name = productSnapshot.child("name").getValue(String::class.java) ?: ""
                    val description =
                        productSnapshot.child("description").getValue(String::class.java) ?: ""
                    val quantity = productSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                    val unitPrice =
                        productSnapshot.child("unitPrice").getValue(String::class.java) ?: "0.0"
                    val timestamp =
                        productSnapshot.child("timestamp").getValue(Long::class.java) ?: 0
                    val product = Product(key, name, description, quantity, unitPrice, timestamp)
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

    private fun showMessage(message: String) {
        // Show message using Snackbar or Toast
        // For simplicity, we'll use a Snackbar here
        val view = requireActivity().findViewById<View>(android.R.id.content)
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showAddProductDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val editTextProductName = dialogView.findViewById<EditText>(R.id.editTextProductName)
        val editTextProductDescription =
            dialogView.findViewById<EditText>(R.id.editTextProductDescription)
        val editTextProductQuantity =
            dialogView.findViewById<EditText>(R.id.editTextProductQuantity)
        val editTextProductUnitPrice =
            dialogView.findViewById<EditText>(R.id.editTextProductUnitPrice)
        val buttonAddProduct = dialogView.findViewById<Button>(R.id.buttonAddProduct)
        val closeButton =
            dialogView.findViewById<TextView>(R.id.close) // TextView acting as a close button

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Product")
            .setView(dialogView)
            .setCancelable(true)
            .create()

        closeButton.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog when the close button is clicked
        }

        buttonAddProduct.setOnClickListener {
            val productName = editTextProductName.text.toString()
            val productDescription = editTextProductDescription.text.toString()
            val productQuantity = editTextProductQuantity.text.toString().toIntOrNull()
            var productUnitPrice = editTextProductUnitPrice.text.toString()

            // Format unit price to have two decimal places if it's a whole number
            val priceDouble = productUnitPrice.toDoubleOrNull()
            if (priceDouble != null && priceDouble == priceDouble.toInt().toDouble()) {
                // If the input is a whole number, format it to have two decimal places
                productUnitPrice = String.format("%.2f", priceDouble)
            }

            if (productName.isNotEmpty() && productDescription.isNotEmpty() && productQuantity != null && productUnitPrice.isNotEmpty()) {
                // Generate a random 4-digit key
                val productKey = (1000..9999).random().toString()

                // Get current timestamp
                val timestamp = System.currentTimeMillis()

                // Create product object with timestamp
                val product = Product(
                    productKey,
                    productName,
                    productDescription,
                    productQuantity,
                    productUnitPrice,
                    timestamp
                )

                // Add product to Firebase Realtime Database using the random key
                productsRef.child(productKey).setValue(product)
                    .addOnSuccessListener {
                        showMessage("Product added successfully.")
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        showMessage("Failed to add product. Please try again later.")
                    }
            } else {
                showMessage("Please fill in all fields.")
            }
        }

        dialog.show()
    }
}