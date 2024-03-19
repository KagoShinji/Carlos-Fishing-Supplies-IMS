package com.example.carlosfishingsuppliesims.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carlosfishingsuppliesims.R
import com.example.carlosfishingsuppliesims.models.Product
import com.google.android.material.snackbar.Snackbar

class MyAdapter(
    private val editClickListener: (Int) -> Unit,
    private val deleteClickListener: (String) -> Unit // Pass product key for delete action
) : RecyclerView.Adapter<MyAdapter.ProductViewHolder>() {

    private var productList: MutableList<Product> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun updateProductList(products: List<Product>) {
        productList.clear()
        productList.addAll(products)
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val keyTextView: TextView = itemView.findViewById(R.id.product_id)
        private val nameTextView: TextView = itemView.findViewById(R.id.productName)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.productDescription)
        private val quantityTextView: TextView = itemView.findViewById(R.id.quantity)
        private val unitPriceTextView: TextView = itemView.findViewById(R.id.productPrice)
        private val moreImageView: ImageView = itemView.findViewById(R.id.more)

        init {
            // Set click listener for the "more" ImageView
            moreImageView.setOnClickListener { showPopupMenu(it) }
        }

        fun bind(product: Product) {
            // Bind product data to TextViews
            keyTextView.text = product.key.toString()
            nameTextView.text = product.name
            descriptionTextView.text = product.description
            quantityTextView.text = product.quantity.toString()
            unitPriceTextView.text = product.unitPrice // Display unit price as string
        }

        private fun showPopupMenu(view: View) {
            // Create and show PopupMenu for the "more" ImageView
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.products_menu) // Inflate menu resource
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.editProduct -> {
                        // Call editClickListener with adapter position when Edit Product is clicked
                        editClickListener(adapterPosition)
                        showSnackBar("Edit clicked")
                        true
                    }
                    R.id.deleteProduct -> {
                        // Call deleteClickListener with product key when Delete Product is clicked
                        val productKey = productList[adapterPosition].key.toString()
                        deleteClickListener(productKey)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        private fun showSnackBar(message: String) {
            // Show Snackbar with the given message
            val snackbar = Snackbar.make(itemView, message, Snackbar.LENGTH_SHORT)
            snackbar.show()
        }
    }
}
