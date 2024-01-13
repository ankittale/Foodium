package com.labs.foodium.data

import com.labs.foodium.data.db.RecipesDao
import com.labs.foodium.data.db.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataStore @Inject constructor(private val recipesDao: RecipesDao) {

    fun readDatabase() : Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }
}