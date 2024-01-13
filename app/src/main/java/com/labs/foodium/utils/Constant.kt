package com.labs.foodium.utils

class Constants {
    companion object {

        //Base
        const val BASE_URL = "https://api.spoonacular.com"
        const val API_KEY = "666ee28f8ce94831b3d3b4893fd2c8d9"

        //Query
        const val QUERY_NUMBER = "number"
        const val QUERY_API_KEY = "apiKey"
        const val QUERY_TYPE = "type"
        const val QUERY_DIET = "diet"
        const val QUERY_ADD_RECIPE_INFORMATION = "addRecipeInformation"
        const val QUERY_FILL_INGREDIENTS = "fillIngredients"

        // ROOM Database
        const val DATABASE_NAME = "recipes_database"
        const val RECIPES_TABLE = "recipes_table"
    }

}