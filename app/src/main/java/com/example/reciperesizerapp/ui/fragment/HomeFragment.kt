package com.example.reciperesizerapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reciperesizerapp.R
import com.example.reciperesizerapp.data.model.Category
import com.example.reciperesizerapp.data.model.Meal
import com.example.reciperesizerapp.ui.adapter.CategoryAdapter
import com.example.reciperesizerapp.ui.adapter.RecipeCardAdapter
import com.example.reciperesizerapp.ui.viewmodel.RecipeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText

class HomeFragment : Fragment() {

    private val viewModel: RecipeViewModel by activityViewModels()
    
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var searchEditText: TextInputEditText
    private lateinit var suggestionsChipGroup: ChipGroup
    private lateinit var progressBar: ProgressBar
    
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var recipeAdapter: RecipeCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView)
        recipesRecyclerView = view.findViewById(R.id.recipesRecyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)
        suggestionsChipGroup = view.findViewById(R.id.suggestionsChipGroup)
        progressBar = view.findViewById(R.id.progressBar)
        
        setupCategoriesRecyclerView()
        setupRecipesRecyclerView()
        setupSearchBar()
        setupSearchSuggestions()
        observeViewModel()
    }
    
    private fun setupCategoriesRecyclerView() {
        categoryAdapter = CategoryAdapter(emptyList()) { category ->
            onCategorySelected(category)
        }
        
        categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
    }
    
    private fun setupRecipesRecyclerView() {
        recipeAdapter = RecipeCardAdapter { meal ->
            onRecipeSelected(meal)
        }
        
        recipesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recipeAdapter
        }
    }
    
    private fun setupSearchBar() {
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotBlank()) {
                    viewModel.searchRecipe(query)
                    // Navigate to detail fragment
                    findNavController().navigate(R.id.action_homeFragment_to_recipeDetailFragment)
                }
                true
            } else {
                false
            }
        }
    }
    
    private fun setupSearchSuggestions() {
        val suggestions = resources.getStringArray(R.array.popular_recipe_suggestions)
        
        suggestionsChipGroup.removeAllViews()
        
        suggestions.forEach { suggestion ->
            val chip = layoutInflater.inflate(
                R.layout.item_search_suggestion,
                suggestionsChipGroup,
                false
            ) as Chip
            
            chip.text = suggestion
            chip.setOnClickListener {
                searchEditText.setText(suggestion)
                viewModel.searchRecipe(suggestion)
                // Navigate to detail fragment
                findNavController().navigate(R.id.action_homeFragment_to_recipeDetailFragment)
            }
            
            suggestionsChipGroup.addView(chip)
        }
    }
    
    private fun observeViewModel() {
        // Observe categories
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter = CategoryAdapter(categories) { category ->
                onCategorySelected(category)
            }
            categoriesRecyclerView.adapter = categoryAdapter
        }
        
        // Observe popular recipes
        viewModel.popularRecipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.submitList(recipes)
        }
        
        // Observe category recipes
        viewModel.categoryRecipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.submitList(recipes)
        }
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    
    private fun onCategorySelected(category: Category) {
        viewModel.loadRecipesByCategory(category)
    }
    
    private fun onRecipeSelected(meal: Meal) {
        viewModel.searchRecipe(meal.name)
        // Navigate to detail fragment
        findNavController().navigate(R.id.action_homeFragment_to_recipeDetailFragment)
    }
}
