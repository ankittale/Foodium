package com.labs.foodium.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.labs.foodium.models.FoodRecipe
import com.labs.foodium.models.Result

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

    @TypeConverter
    fun resultToString(result: Result): String {
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToResult(data: String): Result {
        val listType = object : TypeToken<Result>() {}.type
        return gson.fromJson(data, listType)
    }
}