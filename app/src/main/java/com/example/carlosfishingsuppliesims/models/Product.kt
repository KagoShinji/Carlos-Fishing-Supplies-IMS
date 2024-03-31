package com.example.carlosfishingsuppliesims.models

data class Product(
    var key: String,
    var name: String? = null,
    var description: String? = null,
    var quantity: Int,
    var unitPrice: String,
    var timestamp: Long,
)
