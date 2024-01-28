package com.labs.foodium.adapter

import android.graphics.Color
import android.text.style.BackgroundColorSpan
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.labs.foodium.R
import com.labs.foodium.data.db.entity.FavouriteEntity
import com.labs.foodium.databinding.FavoriteRecipeRowLayoutBinding
import com.labs.foodium.ui.fragment.FavouriteRecipeFragmentDirections
import com.labs.foodium.utils.RecipesDiffUtil
import com.labs.foodium.viewmodel.FoodViewModel

class FavoriteRecipeAdapter(
    private val requireActivity: FragmentActivity,
    private val foodViewModel: FoodViewModel
): RecyclerView.Adapter<FavoriteRecipeAdapter.FavoriteHolder>(), ActionMode.Callback {

    private var multiSelection = false
    private lateinit var appActionMode: ActionMode
    private lateinit var rootView: View
    private var viewHolderColorSet = arrayListOf<FavoriteHolder>()
    private var selectedRecipes = arrayListOf<FavouriteEntity>()
    private var favoriteRecipes = emptyList<FavouriteEntity>()

    class FavoriteHolder(val binding: FavoriteRecipeRowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(favouriteEntity: FavouriteEntity) {
            binding.favoriteEntity = favouriteEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): FavoriteHolder {
                val layoutInflater =  LayoutInflater.from(parent.context)
                val binding = FavoriteRecipeRowLayoutBinding.inflate(layoutInflater, parent, false)
                return FavoriteHolder((binding))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteHolder {
        return FavoriteHolder.from(parent)
    }

    private fun applySelection(holder: FavoriteHolder, contentRecipe: FavouriteEntity) {
        if (selectedRecipes.contains(contentRecipe)){
            selectedRecipes.remove(contentRecipe)
            changeRecipes(holder, R.color.cardBackgroundColor, R.color.strokeColor)
            applyActionModeTitle()
        } else {
            changeRecipes(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
            selectedRecipes.add(contentRecipe)
            applyActionModeTitle()
        }
    }

    private fun changeRecipes(holder: FavoriteHolder, backgroundColor: Int, strokeColor:Int) {
        holder.binding.favoriteRecipesRowLayout.setBackgroundColor(ContextCompat.getColor(requireActivity, backgroundColor))
        holder.binding.favoriteRowRecipes.strokeColor = ContextCompat.getColor(requireActivity, strokeColor)
    }

    //Action Mode Applied Here
    
    private fun applyActionModeTitle() {
        when (selectedRecipes.size) {
            0 -> {
                multiSelection = false
                appActionMode.finish()
            }
            1 -> {
                appActionMode.title = "${selectedRecipes.size} item selected"
            }
            else -> {
                appActionMode.title = "${selectedRecipes.size} items selected"
            }
        }
    }

    override fun getItemCount(): Int {
         return favoriteRecipes.size
    }

    override fun onBindViewHolder(holder: FavoriteHolder, position: Int) {

        viewHolderColorSet.add(holder)
        rootView = holder.itemView.rootView

        val currentRecipes = favoriteRecipes[position]
        holder.bind(currentRecipes)

        saveItemStateOnScroll(currentRecipes, holder)

        /* Single CLick fragment */
        holder.binding.favoriteRecipesRowLayout.setOnClickListener{
            if (multiSelection) {
                applySelection(holder, currentRecipes)
            } else {
                val action =
                    FavouriteRecipeFragmentDirections.actionFavouriteRecipeFragmentToDetailsActivity(currentRecipes.result)
                holder.itemView.findNavController().navigate(action)
            }

        }

        /* Long CLick fragment */
        holder.binding.favoriteRecipesRowLayout.setOnLongClickListener {
            if (!multiSelection) {
                multiSelection = true
                requireActivity.startActionMode(this)
                applySelection(holder, currentRecipes)
                true
            } else {
                applySelection(holder, currentRecipes)
                true
            }
        }

    }

    private fun saveItemStateOnScroll(contentRecipe: FavouriteEntity, holder: FavoriteHolder) {
        if (selectedRecipes.contains(contentRecipe)){
            changeRecipes(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
        } else {
            changeRecipes(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorite_contextual_menu, menu)
        appActionMode = actionMode!!
        applyStatusBarColor(R.color.contextualStatusBarColor)
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.delete_favorite_recipe_menu -> {
                selectedRecipes.forEach {
                    foodViewModel.deleteFavouriteRecipes(it)
                }
                "${selectedRecipes.size} Recipes removed".showSnackMessage()
                multiSelection = false
                selectedRecipes.clear()
                appActionMode.finish()
            }
        }
        return true
    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {
        multiSelection = false
        selectedRecipes.clear()
        viewHolderColorSet.forEach {
            favoriteHolder ->  changeRecipes(favoriteHolder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
        applyStatusBarColor(R.color.statusBarColor)
    }

    private fun applyStatusBarColor(color: Int) {
        requireActivity.window.statusBarColor = ContextCompat.getColor(requireActivity, color)
    }
    fun setData(newFavoriteRecipe: List<FavouriteEntity>){
        val recipeDiffUtil = RecipesDiffUtil(favoriteRecipes, newFavoriteRecipe)
        val diffUtil = DiffUtil.calculateDiff(recipeDiffUtil)
        favoriteRecipes = newFavoriteRecipe
        diffUtil.dispatchUpdatesTo(this)
    }

    private fun String.showSnackMessage() {
        Snackbar.make(rootView, this, Snackbar.LENGTH_LONG)
            .setAction("Ok") {}
            .show()
    }

    fun clearContextualActionMode() {
        if (this::appActionMode.isInitialized){
            appActionMode.finish()
        }
    }
}