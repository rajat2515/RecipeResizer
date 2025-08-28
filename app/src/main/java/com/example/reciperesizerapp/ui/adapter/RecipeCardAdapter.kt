package com.example.reciperesizerapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reciperesizerapp.R
import com.example.reciperesizerapp.data.model.Meal

class RecipeCardAdapter(private val onRecipeClick: (Meal) -> Unit) : 
    ListAdapter<Meal, RecipeCardAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipeImageView)
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipeNameTextView)
        private val recipeCategoryTextView: TextView = itemView.findViewById(R.id.recipeCategoryTextView)

        fun bind(meal: Meal) {
            recipeNameTextView.text = meal.name
            recipeCategoryTextView.text = meal.category ?: meal.area ?: ""
            
            // Load image
            meal.imageUrl?.let { url ->
                Glide.with(itemView.context)
                    .load(url)
                    .centerCrop()
                    .into(recipeImageView)
            }
            
            // Set click listener
            itemView.setOnClickListener {
                onRecipeClick(meal)
            }
        }
    }

    class RecipeDiffCallback : DiffUtil.ItemCallback<Meal>() {
        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem == newItem
        }
    }
}
