package com.example.reciperesizerapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.reciperesizerapp.R
import com.example.reciperesizerapp.data.model.Category
import com.google.android.material.card.MaterialCardView

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryCard: MaterialCardView = itemView.findViewById(R.id.categoryCard)
        private val categoryBackground: LinearLayout = itemView.findViewById(R.id.categoryBackground)
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)

        fun bind(category: Category, isSelected: Boolean) {
            categoryNameTextView.text = category.name
            
            // Set background color
            val colorRes = category.colorResId
            categoryBackground.setBackgroundColor(
                ContextCompat.getColor(itemView.context, colorRes)
            )
            
            // Set card elevation based on selection
            categoryCard.elevation = if (isSelected) 8f else 4f
            
            // Add a stroke to the selected item
            if (isSelected) {
                categoryCard.strokeWidth = 2
                categoryCard.strokeColor = ContextCompat.getColor(itemView.context, R.color.accent)
            } else {
                categoryCard.strokeWidth = 0
            }
            
            // Set click listener
            itemView.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = adapterPosition
                
                // Update the previously selected and newly selected items
                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)
                
                onCategoryClick(category)
            }
        }
    }
}
