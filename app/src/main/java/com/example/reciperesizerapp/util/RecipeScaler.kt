package com.example.reciperesizerapp.util

import com.example.reciperesizerapp.data.model.IngredientMeasure
import java.util.regex.Pattern

object RecipeScaler {
    // Default serving size
    private const val DEFAULT_SERVING_SIZE = 2
    
    // Pattern to match numeric values in measurements
    private val numberPattern = Pattern.compile("(\\d+(?:\\.\\d+)?)")
    
    // Pattern to match fractions like 1/2, 1/4, etc.
    private val fractionPattern = Pattern.compile("(\\d+)/(\\d+)")
    
    /**
     * Scale the ingredients based on the desired number of servings
     * @param ingredients List of ingredient-measure pairs
     * @param servingSize The desired number of servings
     * @return List of scaled ingredient-measure pairs
     */
    fun scaleIngredients(
        ingredients: List<IngredientMeasure>,
        servingSize: Int
    ): List<IngredientMeasure> {
        val scaleFactor = servingSize.toDouble() / DEFAULT_SERVING_SIZE
        
        return ingredients.map { ingredientMeasure ->
            val originalMeasure = ingredientMeasure.measure ?: ""
            val scaledMeasure = scaleMeasurement(originalMeasure, scaleFactor)
            
            ingredientMeasure.copy(scaledMeasure = scaledMeasure)
        }
    }
    
    /**
     * Scale a single measurement string by the given factor
     * @param measurement The original measurement string (e.g., "2 cups", "1/2 tsp")
     * @param factor The scaling factor
     * @return The scaled measurement as a string
     */
    private fun scaleMeasurement(measurement: String, factor: Double): String {
        // Check for empty measurements
        if (measurement.isBlank()) return measurement
        
        // Try to find a numeric value in the measurement
        val numberMatcher = numberPattern.matcher(measurement)
        if (numberMatcher.find()) {
            val originalValue = numberMatcher.group(1)?.toDoubleOrNull() ?: return measurement
            val scaledValue = originalValue * factor
            
            // Format the scaled value (round to 2 decimal places if needed)
            val formattedValue = formatNumber(scaledValue)
            
            // Replace the original number with the scaled value
            return measurement.replaceFirst(numberMatcher.group(1) ?: "", formattedValue)
        }
        
        // Check for fractions
        val fractionMatcher = fractionPattern.matcher(measurement)
        if (fractionMatcher.find()) {
            val numerator = fractionMatcher.group(1)?.toDoubleOrNull() ?: return measurement
            val denominator = fractionMatcher.group(2)?.toDoubleOrNull() ?: return measurement
            
            val originalValue = numerator / denominator
            val scaledValue = originalValue * factor
            
            // Format the scaled value
            val formattedValue = formatNumber(scaledValue)
            
            // Replace the original fraction with the scaled value
            val group1 = fractionMatcher.group(1) ?: ""
            val group2 = fractionMatcher.group(2) ?: ""
            return measurement.replaceFirst("$group1/$group2", formattedValue)
        }
        
        // If no numeric value found, return the original measurement
        return measurement
    }
    
    /**
     * Format a number to be more readable
     * - Whole numbers are displayed without decimal points
     * - Decimal numbers are rounded to 2 decimal places
     */
    private fun formatNumber(number: Double): String {
        return if (number == number.toInt().toDouble()) {
            // It's a whole number
            number.toInt().toString()
        } else {
            // It's a decimal, round to 2 decimal places
            String.format("%.2f", number).replace(".00", "")
        }
    }
}
