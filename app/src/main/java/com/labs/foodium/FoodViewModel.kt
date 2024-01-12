package com.labs.foodium

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.labs.foodium.data.FoodRepository
import com.labs.foodium.models.FoodRecipe
import com.labs.foodium.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val repository: FoodRepository, application: Application ) : AndroidViewModel(application) {

    var recipeResponse: MutableLiveData<NetworkResult<FoodRecipe>?> = MutableLiveData()

    //Get Recipes
    fun getRecipes(queryMap: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queryMap)
    }

    //Done SafeCall for Network
    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        if (hasNetworkConnection()){
            try{
                val response = repository.remote.getRecipes(queries)
                recipeResponse.value = handleFoodRecipeResponse(response)
            }catch (e: Exception){
                recipeResponse.value = NetworkResult.Error("Recipes Not Found")
            }
        } else {
            recipeResponse.value = NetworkResult.Error("No Internet Network")
        }
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