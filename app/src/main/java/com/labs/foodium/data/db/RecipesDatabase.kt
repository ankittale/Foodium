package com.labs.foodium.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [RecipesEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecipesConverter::class)
abstract class RecipesDatabase : RoomDatabase() {

    abstract fun recipeDao() : RecipesDao
}