package com.example.carlosfishingsuppliesims.models

data class ProductSales(
    val productName: String,
    val quantity: Int,
    val totalPrice: Double // Total price for this specific product in the sale
)