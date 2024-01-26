package com.labs.foodium.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.labs.foodium.data.FoodRepository
import com.labs.foodium.data.db.entity.FavouriteEntity
import com.labs.foodium.data.db.entity.RecipesEntity
import com.labs.foodium.models.FoodRecipe
import com.labs.foodium.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val repository: FoodRepository, application: Application ) : AndroidViewModel(application) {

    /*ROOM*/
    val readRecipe: LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()

    val readFavouriteRecipes:LiveData<List<FavouriteEntity>> = repository.local.readFavoriteRecipes().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertRecipes(recipesEntity)
    }

    fun insertFavouriteRecipes(favouriteEntity: FavouriteEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertFavoriteRecipes(favouriteEntity)
    }

    fun deleteFavouriteRecipes(favouriteEntity: FavouriteEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.deleteFavoriteRecipe(favouriteEntity)
    }

    fun deleteAllFavouriteRecipes() = viewModelScope.launch(Dispatchers.IO) {
        repository.local.deleteAllFavoriteRecipes()
    }

    /*RETROFIT*/
    var recipeResponse: MutableLiveData<NetworkResult<FoodRecipe>?> = MutableLiveData()

    var searchResponse: MutableLiveData<NetworkResult<FoodRecipe>?> = MutableLiveData()

    fun searchRecipes(queryMap: Map<String, String>) = viewModelScope.launch {
        getSafeRecipeSearch(queryMap)
    }

    //Get Recipes
    fun getRecipes(queryMap: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queryMap)
    }

    //Done SafeCall for Network
    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipeResponse.value = NetworkResult.Loading()

        if (hasNetworkConnection()){
            try{
                val response = repository.remote.getRecipes(queries)
                recipeResponse.value = handleFoodRecipeResponse(response)

                val foodRecipe = recipeResponse.value!!.data
                if (foodRecipe != null) {
                    offlineCacheHolder(foodRecipe)
                }
            }catch (e: Exception){
                recipeResponse.value = NetworkResult.Error("Recipes Not Found")
            }
        } else {
            recipeResponse.value = NetworkResult.Error("No Internet Network")
        }
    }

    private suspend fun getSafeRecipeSearch(searchQuery: Map<String, String>){
        searchResponse.value = NetworkResult.Loading()

        if (hasNetworkConnection()){
            try{
                val response = repository.remote.searchRecipes(searchQuery)
                searchResponse.value = handleFoodRecipeResponse(response)
            }catch (e: Exception){
                searchResponse.value = NetworkResult.Error("Recipes Not Found")
            }
        } else {
            searchResponse.value = NetworkResult.Error("No Internet Network")
        }
    }

    private fun offlineCacheHolder(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    //Handle Response
    private fun handleFoodRecipeResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        when{
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited")
            }
            response.body()!!.results.isEmpty() -> {
                return NetworkResult.Error("Recipes Not Found")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    //Check connection status
    private fun hasNetworkConnection(): Boolean {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}