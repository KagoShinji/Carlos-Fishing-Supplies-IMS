package com.example.carlosfishingsuppliesims.models

data class Product(
    val key: String = "",
    val name: String? = null,
    val description: String? = null,
    val quantity: Int = 0,
    val unitPrice: String? = null,
    val timestamp: Long = 0,
    val lowStock: Boolean = false,
    val outOfStock: Boolean = false
)

