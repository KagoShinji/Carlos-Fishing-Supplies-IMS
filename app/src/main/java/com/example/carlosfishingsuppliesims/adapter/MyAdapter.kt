package com.example.carlosfishingsuppliesims.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carlosfishingsuppliesims.R
import com.example.carlosfishingsuppliesims.models.Product
import java.util.*

class MyAdapter : RecyclerView.Adapter<MyAdapter.ProductViewHolder>() {

    private var productList: MutableList<Product> = mutableListOf()
    private var productListFull: List<Product> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size

    fun updateProductList(products: List<Product>) {
        productList.clear()
        productList.addAll(products)
        productListFull = ArrayList(products)
        notifyDataSetChanged()
    }

    fun filter(text: String?) {
        productList.clear()
        if (text.isNullOrBlank()) {
            productList.addAll(productListFull)
        } else {
            val query = text.toLowerCase(Locale.getDefault()).trim()
            productList.addAll(productListFull.filter {
                it.name?.toLowerCase(Locale.getDefault())?.contains(query) ?: false
            })
        }
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val keyTextView: TextView = itemView.findViewById(R.id.product_id)
        private val nameTextView: TextView = itemView.findViewById(R.id.productName)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.productDescription)
        private val quantityTextView: TextView = itemView.findViewById(R.id.quantity)
        private val unitPriceTextView: TextView = itemView.findViewById(R.id.productPrice)
        private val lowStockIndicator: ImageView = itemView.findViewById(R.id.lowstockAlert)
        private val noStockIndicator: ImageView = itemView.findViewById(R.id.nostockAlert)

        fun bind(product: Product) {
            keyTextView.text = product.key
            nameTextView.text = product.name
            descriptionTextView.text = product.description
            quantityTextView.text = product.quantity.toString()
            unitPriceTextView.text = product.unitPrice

            lowStockIndicator.visibility = if (product.quantity < 5 && product.quantity > 0) {
                View.VISIBLE
            } else {
                View.GONE
            }

            noStockIndicator.visibility = if (product.quantity == 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}
