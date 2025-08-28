package com.example.reciperesizerapp.data.repository

import com.example.reciperesizerapp.data.api.RetrofitClient
import com.example.reciperesizerapp.data.model.CategoryListResponse
import com.example.reciperesizerapp.data.model.Meal
import com.example.reciperesizerapp.data.model.RecipeResponse
import retrofit2.Response

class RecipeRepository {
    private val apiService = RetrofitClient.recipeApiService
    
    suspend fun searchRecipeByName(recipeName: String): Response<RecipeResponse> {
        return apiService.searchRecipeByName(recipeName)
    }
    
    suspend fun getRecipeById(recipeId: String): Response<RecipeResponse> {
        return apiService.getRecipeById(recipeId)
    }
    
    suspend fun getRandomRecipe(): Response<RecipeResponse> {
        return apiService.getRandomRecipe()
    }
    
    suspend fun getCategories(): Response<CategoryListResponse> {
        return apiService.getCategories()
    }
    
    suspend fun getCategoryList(): Response<CategoryListResponse> {
        return apiService.getCategoryList()
    }
    
    suspend fun getAreaList(): Response<CategoryListResponse> {
        return apiService.getAreaList()
    }
    
    suspend fun getRecipesByCategory(category: String): Response<RecipeResponse> {
        return apiService.getRecipesByCategory(category)
    }
    
    suspend fun getRecipesByArea(area: String): Response<RecipeResponse> {
        return apiService.getRecipesByArea(area)
    }
}
