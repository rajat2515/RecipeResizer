package com.example.reciperesizerapp.data.model

import com.google.gson.annotations.SerializedName

data class CategoryListResponse(
    @SerializedName("categories")
    val categories: List<CategoryItem>? = null,
    
    @SerializedName("meals")
    val meals: List<CategoryName>? = null
)

data class CategoryItem(
    @SerializedName("idCategory")
    val id: String,
    
    @SerializedName("strCategory")
    val name: String,
    
    @SerializedName("strCategoryThumb")
    val thumbnailUrl: String?,
    
    @SerializedName("strCategoryDescription")
    val description: String?
)

data class CategoryName(
    @SerializedName("strCategory")
    val category: String? = null,
    
    @SerializedName("strArea")
    val area: String? = null
)
