package com.example.reciperesizerapp.data.model

data class Category(
    val id: String,
    val name: String,
    val colorResId: Int,
    val apiCategory: String? = null
)
