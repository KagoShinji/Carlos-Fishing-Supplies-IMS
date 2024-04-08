// In SalesAdapter.kt
package com.example.carlosfishingsuppliesims.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carlosfishingsuppliesims.R
import com.example.carlosfishingsuppliesims.models.Sales
import java.text.SimpleDateFormat
import java.util.*

class SalesAdapter(private var salesList: List<Sales>) : RecyclerView.Adapter<SalesAdapter.ViewHolder>() {

    private var filteredSalesList: List<Sales> = salesList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sales_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sales = filteredSalesList[position]
        holder.bind(sales)
    }

    override fun getItemCount(): Int {
        return filteredSalesList.size
    }

    fun updateData(newSalesList: List<Sales>) {
        // Sort the new sales list by dateTime in descending order
        val sortedSalesList = newSalesList.sortedByDescending { SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault()).parse(it.dateTime) }
        salesList = sortedSalesList
        filterSalesByDateTime("") // Reset filter
    }

    fun filterSalesByDateTime(query: String) {
        filteredSalesList = if (query.isEmpty()) {
            salesList
        } else {
            salesList.filter { sale ->
                // Parse the date string to extract month, date, and time
                val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
                val date = dateFormat.parse(sale.dateTime)

                // Format date and time strings for comparison
                val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(date)
                val fullDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date)
                val time = SimpleDateFormat("hh:mma", Locale.getDefault()).format(date)

                // Check if any part of the date/time matches the search query
                month.toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault())) ||
                        fullDate.toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault())) ||
                        time.toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault()))
            }
        }
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
