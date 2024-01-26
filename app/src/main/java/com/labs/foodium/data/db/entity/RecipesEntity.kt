package com.labs.foodium.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.labs.foodium.models.FoodRecipe
import com.labs.foodium.utils.Constants.Companion.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(
    var foodRecipe: FoodRecipe
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}