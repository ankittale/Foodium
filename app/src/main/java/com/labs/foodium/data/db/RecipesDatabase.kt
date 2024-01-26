package com.labs.foodium.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.labs.foodium.data.db.entity.FavouriteEntity
import com.labs.foodium.data.db.entity.RecipesEntity

@Database(
    entities = [RecipesEntity::class, FavouriteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecipesConverter::class)
abstract class RecipesDatabase : RoomDatabase() {

    abstract fun recipeDao() : RecipesDao
}