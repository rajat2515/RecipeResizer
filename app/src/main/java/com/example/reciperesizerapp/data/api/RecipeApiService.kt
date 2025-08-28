package com.example.reciperesizerapp.data.api

import com.example.reciperesizerapp.data.model.CategoryListResponse
import com.example.reciperesizerapp.data.model.RecipeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {
    @GET("api/json/v1/1/search.php")
    suspend fun searchRecipeByName(@Query("s") recipeName: String): Response<RecipeResponse>
    
    @GET("api/json/v1/1/lookup.php")
    suspend fun getRecipeById(@Query("i") recipeId: String): Response<RecipeResponse>
    
    @GET("api/json/v1/1/random.php")
    suspend fun getRandomRecipe(): Response<RecipeResponse>
    
    @GET("api/json/v1/1/categories.php")
    suspend fun getCategories(): Response<CategoryListResponse>
    
    @GET("api/json/v1/1/filter.php")
    suspend fun getRecipesByCategory(@Query("c") category: String): Response<RecipeResponse>
    
    @GET("api/json/v1/1/filter.php")
    suspend fun getRecipesByArea(@Query("a") area: String): Response<RecipeResponse>
    
    @GET("api/json/v1/1/list.php?c=list")
    suspend fun getCategoryList(): Response<CategoryListResponse>
    
    @GET("api/json/v1/1/list.php?a=list")
    suspend fun getAreaList(): Response<CategoryListResponse>
}
