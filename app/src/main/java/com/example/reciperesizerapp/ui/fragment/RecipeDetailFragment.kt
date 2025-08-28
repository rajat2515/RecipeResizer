package com.example.reciperesizerapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reciperesizerapp.R
import com.example.reciperesizerapp.ui.adapter.IngredientsAdapter
import com.example.reciperesizerapp.ui.viewmodel.RecipeViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class RecipeDetailFragment : Fragment() {

    private val viewModel: RecipeViewModel by activityViewModels()
    
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var recipeImageView: ImageView
    private lateinit var recipeNameTextView: TextView
    private lateinit var instructionsTextView: TextView
    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var servingSizeEditText: TextInputEditText
    private lateinit var applyButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var backButton: FloatingActionButton
    
    private lateinit var adapter: IngredientsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        collapsingToolbar = view.findViewById(R.id.collapsingToolbar)
        recipeImageView = view.findViewById(R.id.recipeImageView)
        recipeNameTextView = view.findViewById(R.id.recipeNameTextView)
        instructionsTextView = view.findViewById(R.id.instructionsTextView)
        ingredientsRecyclerView = view.findViewById(R.id.ingredientsRecyclerView)
        servingSizeEditText = view.findViewById(R.id.servingSizeEditText)
        applyButton = view.findViewById(R.id.applyButton)
        progressBar = view.findViewById(R.id.progressBar)
        errorTextView = view.findViewById(R.id.errorTextView)
        backButton = view.findViewById(R.id.backButton)
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        adapter = IngredientsAdapter()
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(context)
        ingredientsRecyclerView.adapter = adapter
    }
    
    private fun setupClickListeners() {
        // Apply button for serving size
        applyButton.setOnClickListener {
            val servingSizeText = servingSizeEditText.text.toString()
            if (servingSizeText.isNotBlank()) {
                val servingSize = servingSizeText.toIntOrNull() ?: 2
                if (servingSize > 0) {
                    viewModel.scaleIngredients(servingSize)
                }
            }
        }
        
        // Back button
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun observeViewModel() {
        // Observe recipe
        viewModel.recipe.observe(viewLifecycleOwner) { meal ->
            if (meal != null) {
                // Set recipe name in collapsing toolbar
                collapsingToolbar.title = meal.name
                
                // Show recipe details
                recipeNameTextView.text = meal.name
                instructionsTextView.text = meal.instructions
                
                // Load image
                meal.imageUrl?.let { url ->
                    Glide.with(this)
                        .load(url)
                        .centerCrop()
                        .into(recipeImageView)
                }
                
                // Show recipe view
                errorTextView.visibility = View.GONE
            } else {
                if (viewModel.isLoading.value != true) {
                    errorTextView.visibility = View.VISIBLE
                }
            }
        }
        
        // Observe ingredients
        viewModel.ingredients.observe(viewLifecycleOwner) { ingredients ->
            adapter.submitList(ingredients)
        }
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                errorTextView.visibility = View.GONE
            }
        }
        
        // Observe error messages
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrBlank()) {
                errorTextView.text = errorMessage
                errorTextView.visibility = View.VISIBLE
            } else {
                errorTextView.visibility = View.GONE
            }
        }
    }
}
