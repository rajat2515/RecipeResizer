package com.example.reciperesizerapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reciperesizerapp.R
import com.example.reciperesizerapp.data.model.IngredientMeasure

class IngredientsAdapter : ListAdapter<IngredientMeasure, IngredientsAdapter.IngredientViewHolder>(IngredientDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val measureTextView: TextView = itemView.findViewById(R.id.ingredientMeasureTextView)
        private val nameTextView: TextView = itemView.findViewById(R.id.ingredientNameTextView)

        fun bind(ingredient: IngredientMeasure) {
            // Use the scaled measure if available, otherwise use the original measure
            val measureText = ingredient.scaledMeasure ?: ingredient.measure ?: ""
            measureTextView.text = measureText
            nameTextView.text = ingredient.ingredient ?: ""
        }
    }

    class IngredientDiffCallback : DiffUtil.ItemCallback<IngredientMeasure>() {
        override fun areItemsTheSame(oldItem: IngredientMeasure, newItem: IngredientMeasure): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: IngredientMeasure, newItem: IngredientMeasure): Boolean {
            return oldItem == newItem
        }
    }
}
