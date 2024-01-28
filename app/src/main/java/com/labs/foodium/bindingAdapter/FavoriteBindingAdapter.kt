package com.labs.foodium.bindingAdapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.labs.foodium.adapter.FavoriteRecipeAdapter
import com.labs.foodium.data.db.entity.FavouriteEntity

class FavoriteBindingAdapter {

    companion object {

        @BindingAdapter("setVisibility", "setData", requireAll = false)
        @JvmStatic
        fun setVisibility(view: View, favoritesEntity: List<FavouriteEntity>?, mAdapter: FavoriteRecipeAdapter?) {
            when (view) {
                is RecyclerView -> {
                    val dataCheck = favoritesEntity.isNullOrEmpty()
                    view.isInvisible = dataCheck
                    if(!dataCheck){
                        favoritesEntity?.let { mAdapter?.setData(it) }
                    }
                }
                else -> view.isVisible = favoritesEntity.isNullOrEmpty()
            }
        }
    }
}