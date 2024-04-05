package com.example.carlosfishingsuppliesims.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carlosfishingsuppliesims.R
import com.example.carlosfishingsuppliesims.models.Product
import com.google.firebase.database.*

class DashboardAdapter(private val context: Context) : RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {

    private val productList: MutableList<Product> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dashboard_item, parent, false)
        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size

    inner class DashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.dashboardName)
        private val quantityTextView: TextView = itemView.findViewById(R.id.dashboard_quantity)
        private val priceTextView: TextView = itemView.findViewById(R.id.dashboardPrice)

        fun bind(product: Product) {
            nameTextView.text = product.name
            quantityTextView.text = product.quantity.toString()
            priceTextView.text = product.unitPrice
        }
    }

    init {
        fetchProductsFromFirebase()
    }

    private fun fetchProductsFromFirebase() {
        val productsRef = FirebaseDatabase.getInstance().getReference("products")

        // Query to fetch products based on timestamp (assuming timestamp is stored as a child node)
        val query: Query =
            productsRef.orderByChild("timestamp").limitToLast(10) // Fetching 10 latest products

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val reversedList =
                    mutableListOf<Product>() // Create a new list to store reversed products
                for (snapshot in dataSnapshot.children) {
                    val product = snapshot.getValue(Product::class.java)
                    product?.let {
                        reversedList.add(0, product) // Add products to the beginning of the list
                    }
                }
                productList.clear() // Clear the existing list
                productList.addAll(reversedList) // Add reversed products to the productList
                notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }
}
