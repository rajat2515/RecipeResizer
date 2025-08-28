package com.example.reciperesizerapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reciperesizerapp.R
import com.example.reciperesizerapp.data.model.Category
import com.example.reciperesizerapp.data.model.CategoryItem
import com.example.reciperesizerapp.data.model.CategoryName
import com.example.reciperesizerapp.data.model.IngredientMeasure
import com.example.reciperesizerapp.data.model.Meal
import com.example.reciperesizerapp.data.repository.RecipeRepository
import com.example.reciperesizerapp.util.RecipeScaler
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {
    private val repository = RecipeRepository()
    
    // LiveData for recipe search results
    private val _recipe = MutableLiveData<Meal?>()
    val recipe: LiveData<Meal?> = _recipe
    
    // LiveData for scaled ingredients
    private val _ingredients = MutableLiveData<List<IngredientMeasure>>(emptyList())
    val ingredients: LiveData<List<IngredientMeasure>> = _ingredients
    
    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData for error messages
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // LiveData for categories
    private val _categories = MutableLiveData<List<Category>>(emptyList())
    val categories: LiveData<List<Category>> = _categories
    
    // LiveData for popular recipes
    private val _popularRecipes = MutableLiveData<List<Meal>>(emptyList())
    val popularRecipes: LiveData<List<Meal>> = _popularRecipes
    
    // LiveData for category recipes
    private val _categoryRecipes = MutableLiveData<List<Meal>>(emptyList())
    val categoryRecipes: LiveData<List<Meal>> = _categoryRecipes
    
    // Current serving size
    private var currentServingSize = 2
    
    // Initialize with default categories
    init {
        loadDefaultCategories()
        loadPopularRecipes()
    }
    
    /**
     * Load default categories
     */
    private fun loadDefaultCategories() {
        val defaultCategories = listOf(
            Category("all", "All", R.color.primary),
            Category("vegetarian", "Vegetarian", R.color.vegetarian, "Vegetarian"),
            Category("non_vegetarian", "Non-Veg", R.color.non_vegetarian),
            Category("vegan", "Vegan", R.color.vegan, "Vegan"),
            Category("dessert", "Dessert", R.color.dessert, "Dessert"),
            Category("seafood", "Seafood", R.color.seafood, "Seafood"),
            Category("breakfast", "Breakfast", R.color.breakfast, "Breakfast"),
            Category("italian", "Italian", R.color.accent, "Italian"),
            Category("indian", "Indian", R.color.accent_dark, "Indian"),
            Category("chinese", "Chinese", R.color.accent_light, "Chinese"),
            Category("mexican", "Mexican", R.color.primary_light, "Mexican"),
            Category("thai", "Thai", R.color.primary_dark, "Thai")
        )
        
        _categories.value = defaultCategories
    }
    
    /**
     * Load API categories
     */
    fun loadApiCategories() {
        viewModelScope.launch {
            try {
                val response = repository.getCategoryList()
                if (response.isSuccessful) {
                    val apiCategories = response.body()?.meals?.mapNotNull { it.category }
                    // Process categories if needed
                }
            } catch (e: Exception) {
                // Handle error
                _error.value = "Error loading categories: ${e.message}"
            }
        }
    }
    
    /**
     * Load popular recipes
     */
    fun loadPopularRecipes() {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                // For demo, we'll just load some random recipes
                val response = repository.getRandomRecipe()
                
                if (response.isSuccessful) {
                    val meals = response.body()?.meals
                    meals?.let { mealList ->
                        if (mealList.isNotEmpty()) {
                            _popularRecipes.value = mealList
                        }
                    }
                }
                
                // Load a few more random recipes to populate the list
                for (i in 1..3) {
                    val additionalResponse = repository.getRandomRecipe()
                    if (additionalResponse.isSuccessful) {
                        val currentList = _popularRecipes.value?.toMutableList() ?: mutableListOf()
                        additionalResponse.body()?.meals?.firstOrNull()?.let { meal ->
                            currentList.add(meal)
                            _popularRecipes.value = currentList
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error loading popular recipes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load recipes by category
     */
    fun loadRecipesByCategory(category: Category) {
        if (category.id == "all") {
            loadPopularRecipes()
            return
        }
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val response = if (category.apiCategory != null) {
                    repository.getRecipesByCategory(category.apiCategory)
                } else {
                    // If no API category is specified, use the name
                    repository.getRecipesByCategory(category.name)
                }
                
                if (response.isSuccessful) {
                    val meals = response.body()?.meals
                    meals?.let { mealList ->
                        if (mealList.isNotEmpty()) {
                            _categoryRecipes.value = mealList
                        } else {
                            _categoryRecipes.value = emptyList()
                            _error.value = "No recipes found for ${category.name}"
                        }
                    } ?: run {
                        _categoryRecipes.value = emptyList()
                        _error.value = "No recipes found for ${category.name}"
                    }
                } else {
                    _error.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                _categoryRecipes.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Search for recipes by name
     */
    fun searchRecipe(recipeName: String) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val response = repository.searchRecipeByName(recipeName)
                
                if (response.isSuccessful) {
                    val meals = response.body()?.meals
                    if (!meals.isNullOrEmpty()) {
                        _recipe.value = meals[0]
                        processIngredients(meals[0])
                    } else {
                        _recipe.value = null
                        _error.value = "No recipe found for '$recipeName'"
                    }
                } else {
                    _error.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Get a random recipe
     */
    fun getRandomRecipe() {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val response = repository.getRandomRecipe()
                
                if (response.isSuccessful) {
                    val meals = response.body()?.meals
                    if (!meals.isNullOrEmpty()) {
                        _recipe.value = meals[0]
                        processIngredients(meals[0])
                    } else {
                        _recipe.value = null
                        _error.value = "No recipe found"
                    }
                } else {
                    _error.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Process ingredients from a meal and scale them to the current serving size
     */
    private fun processIngredients(meal: Meal) {
        val ingredientMeasurePairs = meal.getIngredientMeasurePairs()
        scaleIngredients(ingredientMeasurePairs, currentServingSize)
    }
    
    /**
     * Scale ingredients to the specified serving size
     */
    fun scaleIngredients(servingSize: Int) {
        currentServingSize = servingSize
        _recipe.value?.let { meal ->
            scaleIngredients(meal.getIngredientMeasurePairs(), servingSize)
        }
    }
    
    /**
     * Scale the ingredients and update the LiveData
     */
    private fun scaleIngredients(ingredients: List<IngredientMeasure>, servingSize: Int) {
        val scaledIngredients = RecipeScaler.scaleIngredients(ingredients, servingSize)
        _ingredients.value = scaledIngredients
    }
    
    /**
     * Clear any error message
     */
    fun clearError() {
        _error.value = null
    }
}