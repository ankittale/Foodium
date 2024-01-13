package com.labs.foodium.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.labs.foodium.models.FoodRecipe

class RecipesConverter {

    var gson = Gson()

    @TypeConverter
    fun foodRecipes(foodRecipe: FoodRecipe): String {
        return gson.toJson(foodRecipe)
    }

    @TypeConverter
    fun stringToFoodRecipe(data: String): FoodRecipe{
        val listRecipes = object : TypeToken<FoodRecipe>() {}.type
        return gson.fromJson(data, listRecipes)

    }
}