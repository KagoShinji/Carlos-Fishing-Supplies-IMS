package com.example.carlosfishingsuppliesims.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.carlosfishingsuppliesims.models.Product

class ProductSpinnerAdapter(
    context: Context,
    private val productList: List<String>,
    private val productKeyList: List<String>
) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, productList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false)
        }

        val productName = convertView!!.findViewById<TextView>(android.R.id.text1)
        productName.text = productList[position]

        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        }

        val productName = convertView!!.findViewById<TextView>(android.R.id.text1)
        productName.text = productList[position]

        return convertView
    }

    fun getProductKey(position: Int): String {
        return productKeyList[position]
    }
}
