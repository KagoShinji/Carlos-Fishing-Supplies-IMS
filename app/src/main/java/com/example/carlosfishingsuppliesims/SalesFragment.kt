package com.example.carlosfishingsuppliesims

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carlosfishingsuppliesims.adapter.AddedSalesAdapter
import com.example.carlosfishingsuppliesims.adapter.SalesAdapter
import com.example.carlosfishingsuppliesims.models.Product
import com.example.carlosfishingsuppliesims.models.Sales
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class SalesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var salesAdapter: SalesAdapter
    private lateinit var addedSalesAdapter: AddedSalesAdapter
    private lateinit var addedSalesRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sales, container, false)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize Firebase Realtime Database
        database = Firebase.database.reference

        // Initialize RecyclerView for sales
        val recyclerViewSales: RecyclerView = view.findViewById(R.id.recyclerViewSales)
        recyclerViewSales.layoutManager = LinearLayoutManager(requireContext())
        salesAdapter = SalesAdapter(ArrayList())
        recyclerViewSales.adapter = salesAdapter

        // Initialize RecyclerView for added sales

        // Fetch data from Firebase for sales
        fetchDataFromFirebase()

        // Initialize FloatingActionButton
        val fabAddSales: FloatingActionButton = view.findViewById(R.id.fabAddSales)
        fabAddSales.setOnClickListener {
            // Open the add sales dialog
            showAddSalesDialog()
        }

        return view
    }

    private fun showAddSalesDialog() {
        // Inflating the dialog layout
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_sale, null)

        // Fetch references to the EditText and other UI elements
        val spinnerProducts: Spinner = dialogView.findViewById(R.id.spinnerProducts)
        val editTextQuantity: EditText = dialogView.findViewById(R.id.editTextQuantity)

        // Fetch product data from Firebase and populate the spinner dynamically
        val productNameList = mutableListOf<String>() // List to hold product names
        val productKeyList = mutableListOf<String>() // List to hold product keys
        val productQuantityMap = mutableMapOf<String, Int>() // Map to hold product quantities

        database.child("products").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (productSnapshot in snapshot.children) {
                    val productName = productSnapshot.child("name").getValue(String::class.java)
                    val productKey = productSnapshot.key
                    val productQuantity =
                        productSnapshot.child("quantity").getValue(Int::class.java)

                    productName?.let {
                        productNameList.add(it)
                        productKey?.let { key ->
                            productKeyList.add(key)
                            // Add product quantity to the map
                            productQuantity?.let { quantity ->
                                productQuantityMap[key] = quantity
                            }
                        }
                    }
                }
                // Create and set adapter for product spinner
                val productSpinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    productNameList
                )
                productSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerProducts.adapter = productSpinnerAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })

        // Building and showing the dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add Sales")
            .setPositiveButton("Add") { dialog, which ->
                val selectedPosition = spinnerProducts.selectedItemPosition
                if (selectedPosition != AdapterView.INVALID_POSITION) {
                    val selectedProductName = productNameList[selectedPosition]
                    val selectedProductKey = productKeyList[selectedPosition]
                    val selectedQuantity = editTextQuantity.text.toString().toIntOrNull() ?: 0
                    // Proceed with adding the sale using selectedProductName, selectedProductKey, and selectedQuantity
                    // For now, let's assume you add it to Firebase
                    addSaleToFirebase(selectedProductKey, selectedProductName, selectedQuantity)
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }

        val dialog = dialogBuilder.create()
        dialog.show()
    }


    private fun addSaleToFirebase(productKey: String, productName: String, quantity: Int) {
        // Generate a 4-digit unique random number as the sale ID
        val saleId = generateUniqueSaleId()

        // Get a reference to the "sales" node in your Firebase database
        val salesRef = database.child("sales").child(saleId)

        // Get a reference to the product in the Firebase database to fetch unitPrice
        val productRef = database.child("products").child(productKey)

        // Fetch the unit price of the product
        productRef.child("unitPrice").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val unitPriceString = snapshot.getValue(String::class.java) ?: "0.0"
                val unitPrice = unitPriceString.toDoubleOrNull() ?: 0.0

                // Calculate the total price based on the unit price and quantity
                val totalPrice = unitPrice * quantity

                // Create a map to represent the sale data
                val saleData = mapOf(
                    "dateTime" to getCurrentDateTime(), // Get current date and time
                    "totalPrice" to totalPrice, // Total price based on unit price and quantity
                    "products" to mapOf(
                        // Use the sale ID as the key under products
                        saleId to mapOf(
                            "productName" to productName, // Product name
                            "quantity" to quantity, // Quantity
                            "unitPrice" to unitPrice, // Unit price
                            "totalPrice" to totalPrice // Total price
                        )
                    )
                )

                // Add the sale data to the Firebase database
                salesRef.setValue(saleData)
                    .addOnSuccessListener {
                        // Sale added successfully
                        // You can add any further actions here if needed
                    }
                    .addOnFailureListener { error ->
                        // Error occurred while adding the sale
                        // Handle the error appropriately
                    }

                // Deduct the sold quantity from the product's quantity in the database
                database.child("products").child(productKey).child("quantity")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val currentQuantity = snapshot.getValue(Int::class.java) ?: 0
                            val newQuantity = currentQuantity - quantity
                            if (newQuantity >= 0) {
                                // Update the quantity in the database
                                database.child("products").child(productKey).child("quantity").setValue(newQuantity)
                                    .addOnSuccessListener {
                                        // Quantity updated successfully
                                    }
                                    .addOnFailureListener { error ->
                                        // Error occurred while updating quantity
                                    }
                            } else {
                                // Handle the case where the new quantity becomes negative (out of stock)
                                // You may want to display a message to the user or take other actions
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle database error
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }


    private fun deductQuantityFromProducts(productKey: String, quantityToDeduct: Int) {
        // Get a reference to the product in the Firebase database
        val productRef = database.child("products").child(productKey)

        // Fetch the current quantity of the product
        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentQuantity = snapshot.child("quantity").getValue(Int::class.java) ?: 0

                // Calculate the new quantity after deduction
                val newQuantity = currentQuantity - quantityToDeduct

                // Update the quantity of the product in the database
                productRef.child("quantity").setValue(newQuantity)
                    .addOnSuccessListener {
                        // Quantity deducted successfully
                    }
                    .addOnFailureListener { error ->
                        // Error occurred while deducting quantity
                        // Handle the error appropriately
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
    private fun generateUniqueSaleId(): String {
        // Generate a 4-digit unique random number
        val randomNumber = Random.nextInt(1000, 10000)
        return randomNumber.toString()
    }

    private fun getCurrentDateTime(): String {
        // Implement logic to get current date and time
        // For example:
        // val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        // return dateFormat.format(Date())
        return ""
    }

    private fun calculateTotalPrice(productKey: String, quantity: Int): Double {
        // Implement logic to calculate total price based on product key and quantity
        return 0.00
    }

    private fun fetchDataFromFirebase() {
        val salesList: MutableList<Sales> = mutableListOf()
        database.child("sales").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val key = dataSnapshot.key
                    val dateTime = dataSnapshot.child("dateTime").getValue(String::class.java)
                    val totalPrice = dataSnapshot.child("totalPrice").getValue(Double::class.java)

                    val productsList = mutableListOf<Product>()

                    // Check if the "products" node exists under the current sale
                    if (dataSnapshot.hasChild("products")) {
                        val productsSnapshot = dataSnapshot.child("products")
                        for (productSnapshot in productsSnapshot.children) {
                            val productName =
                                productSnapshot.child("productName").getValue(String::class.java)
                            val quantity =
                                productSnapshot.child("quantity").getValue(Int::class.java)
                            val totalPrice =
                                productSnapshot.child("totalPrice").getValue(Double::class.java)

                            val product = Product(
                                "", // Set an empty string for the key or provide a valid key
                                productName ?: "", // Set the name or provide a default value
                                null, // Set null or provide a value for description if needed
                                quantity ?: 0, // Set the quantity or provide a default value
                                totalPrice?.toString()
                                    ?: "", // Convert price to string and set it as unitPrice or provide a default value
                                0L // Set timestamp to 0 or provide a valid timestamp if needed
                            )
                            productsList.add(product)
                        }
                    }

                    if (key != null && dateTime != null && totalPrice != null) {
                        val sales = Sales(
                            key,
                            dateTime,
                            totalPrice,
                            productsList
                        ) // Pass key to Sales constructor
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