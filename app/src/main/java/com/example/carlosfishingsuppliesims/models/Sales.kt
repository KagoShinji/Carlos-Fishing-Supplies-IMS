package com.example.carlosfishingsuppliesims.models

data class Sales(
    val saleId: String,
    val dateTime: String,
    val totalPrice: Double,
    val products: List<Product>
)