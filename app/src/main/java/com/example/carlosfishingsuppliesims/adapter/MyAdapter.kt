package com.example.carlosfishingsuppliesims.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carlosfishingsuppliesims.R
import com.example.carlosfishingsuppliesims.models.Product

class MyAdapter : RecyclerView.Adapter<MyAdapter.ProductViewHolder>() {

    // List to hold the products
    private var productList: MutableList<Product> = mutableListOf()

    // Create view holder for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    // Bind data to each view holder
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    // Return the total number of items in the list
    override fun getItemCount(): Int {
        return productList.size
    }

    // Update the product list
    fun updateProductList(products: List<Product>) {
        productList.clear()
        productList.addAll(products)
        notifyDataSetChanged()
    }

    // ViewHolder class to hold the views
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views in the product item layout
        private val keyTextView: TextView = itemView.findViewById(R.id.product_id)
        private val nameTextView: TextView = itemView.findViewById(R.id.productName)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.productDescription)
        private val quantityTextView: TextView = itemView.findViewById(R.id.quantity)
        private val unitPriceTextView: TextView = itemView.findViewById(R.id.productPrice)

        // ImageViews for low stock and no stock indicators
        private val lowStockIndicator: ImageView = itemView.findViewById(R.id.lowstockAlert)
        private val noStockIndicator: ImageView = itemView.findViewById(R.id.nostockAlert)

        // Bind product data to views
// Bind product data to views
        fun bind(product: Product) {
            keyTextView.text = product.key
            nameTextView.text = product.name
            descriptionTextView.text = product.description
            quantityTextView.text = product.quantity.toString()
            unitPriceTextView.text = product.unitPrice

            // Set visibility of low stock and no stock indicators based on quantity
            if (product.quantity < 5 && product.quantity > 0) {
                lowStockIndicator.visibility = View.VISIBLE // Show low stock indicator
            } else {
                lowStockIndicator.visibility = View.GONE // Hide low stock indicator
            }
            if (product.quantity == 0) {
                noStockIndicator.visibility = View.VISIBLE // Show no stock indicator
            } else {
                noStockIndicator.visibility = View.GONE // Hide no stock indicator
            }
        }
    }
}
