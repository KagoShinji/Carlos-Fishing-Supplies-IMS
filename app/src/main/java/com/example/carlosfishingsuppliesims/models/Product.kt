package com.example.carlosfishingsuppliesims.models

data class Product(
    var key: String,
    var name: String? = null,
    var description: String? = null,
    var quantity: Int,
    var unitPrice: String,
    var timestamp: Long,
    var productName: String? = null, // Add productName field
    var totalPrice: Double? = null // Add totalPrice field
)
