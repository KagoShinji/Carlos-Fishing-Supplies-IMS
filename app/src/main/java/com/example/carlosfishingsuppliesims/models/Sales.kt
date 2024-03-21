package com.example.carlosfishingsuppliesims.models

data class Sales(
    val key: String,
    val dateTime: String,
    val totalPrice: Double,
    val products: List<Product>
)
