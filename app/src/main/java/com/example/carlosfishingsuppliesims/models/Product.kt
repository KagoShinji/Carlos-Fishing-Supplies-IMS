package com.example.carlosfishingsuppliesims.models

data class Product(
    var key: Int,
    var name: String? = null,
    var description: String? = null,
    var quantity: Int,
    var unitPrice: String // Change unitPrice to String
)
