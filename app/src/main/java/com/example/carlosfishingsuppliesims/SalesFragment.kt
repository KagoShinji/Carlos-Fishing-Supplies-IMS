package com.example.carlosfishingsuppliesims

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carlosfishingsuppliesims.adapter.SalesAdapter
import com.example.carlosfishingsuppliesims.models.Product
import com.example.carlosfishingsuppliesims.models.Sales
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class SalesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var salesAdapter: SalesAdapter
    private val selectedProductsList = mutableListOf<Pair<String, Int>>()
    private lateinit var searchView: SearchView

    // Declare quantityEditText as a property of the SalesFragment class
    private lateinit var quantityEditText: EditText

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

        // Fetch data from Firebase for sales
        fetchDataFromFirebase()


        // Initialize FloatingActionButton
        val fabAddSales: FloatingActionButton = view.findViewById(R.id.fabAddSales)
        fabAddSales.setOnClickListener {
            // Open the add sales dialog
            showAddSalesDialog()
        }

        // Initialize SearchView
        searchView = view.findViewById(R.id.searchViewSales)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                salesAdapter.filterSalesByDateTime(newText ?: "")
                return true
            }
        })

        return view
    }

    private fun showAddSalesDialog() {
        // Inflating the dialog layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_sale, null)

        // Fetch references to the EditText and other UI elements
        val spinnerProducts: Spinner = dialogView.findViewById(R.id.spinnerProducts)
        quantityEditText = dialogView.findViewById(R.id.editTextQuantity) // Initialize quantityEditText here
        val addButton: Button = dialogView.findViewById(R.id.addButton)

        // Fetch product data from Firebase and populate the spinner dynamically
        val productNameList = mutableListOf<String>() // List to hold product names
        val productKeyList = mutableListOf<String>() // List to hold product keys
        val productQuantityMap = mutableMapOf<String, Int>() // Map to hold product quantities

        database.child("products").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (productSnapshot in snapshot.children) {
                    val productName = productSnapshot.child("name").getValue(String::class.java)
                    val productKey = productSnapshot.key
                    val productQuantity = productSnapshot.child("quantity").getValue(Int::class.java)

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

        // Building the dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add Sales")
            .setPositiveButton("Confirm") { dialog, which ->
                // This block is left empty intentionally to avoid automatically adding the product
                // The product will be added only when the user clicks the "Add" button
                addSaleToFirebase()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Clear the selected products list
                selectedProductsList.clear()
                // Clear the input fields for the next product
                quantityEditText.text.clear() // Use quantityEditText here
                // Dismiss the dialog
                dialog.dismiss()
            }

        // Showing the dialog
        val dialog = dialogBuilder.create()
        dialog.show()

        // Set the "Add" button listener
        addButton.setOnClickListener {
            // Call the function to add the selected product to the list
            addSelectedProduct(productKeyList, spinnerProducts, quantityEditText)
        }
    }

    private fun addSelectedProduct(
        productKeyList: List<String>,
        spinner: Spinner,
        quantityEditText: EditText
    ) {
        // Check if any product is selected
        val selectedPosition = spinner.selectedItemPosition
        if (selectedPosition != AdapterView.INVALID_POSITION) {
            val selectedProductName = spinner.selectedItem as String
            val selectedProductKey = productKeyList[selectedPosition]
            val selectedQuantity = quantityEditText.text.toString().toIntOrNull() ?: 0

            // Check if the selected quantity is greater than zero
            if (selectedQuantity > 0) {
                // Add selected product and quantity to the list
                selectedProductsList.add(Pair(selectedProductKey, selectedQuantity))
                // Notify the user that the product has been added
                Toast.makeText(
                    requireContext(),
                    "$selectedProductName added to sale.",
                    Toast.LENGTH_SHORT
                ).show()
                // Clear the input fields for the next product
                quantityEditText.text.clear()
            } else {
                // Show a message indicating that the quantity must be greater than zero
                Toast.makeText(
                    requireContext(),
                    "Quantity must be greater than zero for $selectedProductName.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Show a message to select a product
            Toast.makeText(requireContext(), "Please select a product.", Toast.LENGTH_SHORT).show()
        }
    }



    // Function to send sale data to Firebase
    private fun addSaleToFirebase() {
        // Check if there are any selected products
        if (selectedProductsList.isNotEmpty()) {
            // Confirmation dialog
            val confirmDialog = AlertDialog.Builder(requireContext())
                .setTitle("Confirm Sale")
                .setMessage("Are you sure you want to add the sale?")
                .setPositiveButton("Yes") { dialog, which ->
                    // Proceed to add the sale to the database
                    val saleId = generateUniqueSaleId()
                    addSaleToFirebaseInternal(saleId)
                }
                .setNegativeButton("No") { dialog, which ->
                    // Clear the selected products list
                    selectedProductsList.clear()
                    // Dismiss the dialog
                    dialog.dismiss()
                }
                .create()
            confirmDialog.show()
        } else {
            // Show a message that no products are selected
            Toast.makeText(requireContext(), "No products selected.", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to add sale data to Firebase (internal)
    private fun addSaleToFirebaseInternal(saleId: String) {
        // Create a hashmap to hold sale data
        val saleData = hashMapOf<String, Any>(
            "dateTime" to getCurrentDateTime(), // Add current date and time to the sale data
            "totalPrice" to 0.00, // Initialize total price to 0.00
            "products" to hashMapOf<String, Any>() // Initialize an empty hashmap to hold product data
        )

        // Iterate through the selected products list
        selectedProductsList.forEachIndexed { index, (productKey, quantity) ->
            // Get product details from Firebase based on the product key
            database.child("products").child(productKey)
                .get().addOnSuccessListener { productSnapshot ->
                    // Extract product name and unit price from the product snapshot
                    val productName = productSnapshot.child("name").getValue(String::class.java)
                    val unitPrice = productSnapshot.child("unitPrice").getValue(String::class.java)?.toDoubleOrNull()

                    // Check if product name is not empty and unit price is not null
                    if (!productName.isNullOrBlank() && unitPrice != null) {
                        // Calculate total price for the product based on quantity and unit price
                        val productTotalPrice = unitPrice * quantity

                        // Create a hashmap to hold product data
                        val productData = hashMapOf(
                            "productName" to productName,
                            "quantity" to quantity,
                            "unitPrice" to unitPrice,
                            "totalPrice" to productTotalPrice
                        )

                        // Add product data to the sale data under the "products" key with index as key
                        saleData["products"]?.let {
                            (it as HashMap<String, Any>)[index.toString()] = productData
                        }

                        // Update the total price of the sale
                        val currentTotalPrice = saleData["totalPrice"] as Double
                        saleData["totalPrice"] = currentTotalPrice + productTotalPrice

                        // Deduct the quantity from the original quantity in the database
                        val originalQuantity = productSnapshot.child("quantity").getValue(Int::class.java)
                        if (originalQuantity != null && originalQuantity >= quantity) {
                            database.child("products").child(productKey)
                                .child("quantity").setValue(originalQuantity - quantity)
                                .addOnFailureListener { error ->
                                    // Handle failure to update quantity
                                    Toast.makeText(
                                        requireContext(),
                                        "Error deducting quantity from product: $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            // Handle insufficient quantity error
                            Toast.makeText(
                                requireContext(),
                                "Insufficient quantity for product: $productName",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        // Check if all products have been processed
                        if (index == selectedProductsList.size - 1) {
                            // Add the sale data to the Firebase database under the generated saleId
                            database.child("sales").child(saleId).setValue(saleData)
                                .addOnSuccessListener {
                                    // Sale added successfully
                                    selectedProductsList.clear() // Clear the selected products list
                                    Toast.makeText(
                                        requireContext(),
                                        "Sale added successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Refresh the fragment
                                    fetchDataFromFirebase()
                                }
                                .addOnFailureListener { error ->
                                    // Handle error adding sale to Firebase
                                    Toast.makeText(
                                        requireContext(),
                                        "Error adding sale: $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                }
                .addOnFailureListener { error ->
                    // Handle error getting product details from Firebase
                    Toast.makeText(
                        requireContext(),
                        "Error getting product details: $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    // Function to generate a unique sale ID
    private fun generateUniqueSaleId(): String {
        // Generate a 4-digit unique random number
        val randomNumber = Random.nextInt(1000, 10000)
        return randomNumber.toString()
    }

    // Function to get current date and time
    private fun getCurrentDateTime(): String {
        val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
        val currentDate = Date()
        return simpleDateFormat.format(currentDate)
    }


    // Function to fetch data from Firebase
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
}