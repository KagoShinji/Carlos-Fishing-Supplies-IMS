// In SalesAdapter.kt
package com.example.carlosfishingsuppliesims.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carlosfishingsuppliesims.R
import com.example.carlosfishingsuppliesims.models.Product
import com.example.carlosfishingsuppliesims.models.Sales

class SalesAdapter(private var salesList: List<Sales>) : RecyclerView.Adapter<SalesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sales_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sales = salesList[position]
        holder.bind(sales)
    }

    override fun getItemCount(): Int {
        return salesList.size
    }

    fun updateData(newSalesList: List<Sales>) {
        salesList = newSalesList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val salesID: TextView = itemView.findViewById(R.id.salesID)
        private val salesDateTime: TextView = itemView.findViewById(R.id.salesDateTime)
        private val salesPrice: TextView = itemView.findViewById(R.id.salesPrice)
        private val productsList: TextView = itemView.findViewById(R.id.productsList)

        fun bind(sales: Sales) {
            salesID.text = "${sales.saleId}"
            salesDateTime.text = "${sales.dateTime}"
            salesPrice.text = "₱${sales.totalPrice}"

            val productsText = StringBuilder()
            sales.products.forEachIndexed { index, product ->
                productsText.append("${index + 1}. ${product.name}\n")
                productsText.append("Quantity: ${product.quantity}\n")
                productsText.append("Total Price: ₱${product.unitPrice}\n\n")
            }
            productsList.text = productsText.toString()

        }
    }
}