// AddedSalesAdapter.kt
package com.example.carlosfishingsuppliesims.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carlosfishingsuppliesims.R
import com.example.carlosfishingsuppliesims.models.Sales

class AddedSalesAdapter(private var addedSalesList: List<Sales>) : RecyclerView.Adapter<AddedSalesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.added_sales_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val addedSales = addedSalesList[position]
        holder.bind(addedSales)
    }

    override fun getItemCount(): Int {
        return addedSalesList.size
    }

    fun updateData(newAddedSalesList: List<Sales>) {
        addedSalesList = newAddedSalesList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        private val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)

        fun bind(addedSales: Sales) {
            val productsText = StringBuilder()

            // Iterate over the list of products in the Sales object
            addedSales.products.forEachIndexed { index, product ->
                // Append product name, quantity, and price to the text
                productsText.append("${index + 1}. ${product.name}\n")
                productsText.append("Quantity: ${product.quantity}\n")
                productsText.append("Price: ₱${product.totalPrice}\n\n") // Assuming totalPrice holds the price
            }

            // Set the text to the TextView
            productNameTextView.text = productsText.toString()
            quantityTextView.visibility = View.GONE  // Hide quantity TextView if not needed
            priceTextView.visibility = View.GONE  // Hide price TextView if not needed
        }
    }

}
