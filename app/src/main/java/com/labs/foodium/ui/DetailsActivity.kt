package com.labs.foodium.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.navigation.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.labs.foodium.R
import com.labs.foodium.databinding.ActivityDetailsBinding
import com.labs.foodium.adapter.ViewPagerFragmentAdapter
import com.labs.foodium.data.db.entity.FavouriteEntity
import com.labs.foodium.viewmodel.FoodViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class DetailsActivity: AppCompatActivity() {

    private var recipeSaved = false
    private var savedRecipeId = 0
    private lateinit var appMenuItem: MenuItem
    private val args by navArgs<DetailsActivityArgs>()
    private lateinit var detailsBinding: ActivityDetailsBinding
    private val foodViewModel: FoodViewModel by viewModels()

    private val tabTitle = arrayOf("Overview", "Ingredients", "Instructions")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailsBinding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(detailsBinding.root)

        setSupportActionBar(detailsBinding.detailToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        detailsBinding.recipeViewpager.adapter = ViewPagerFragmentAdapter(supportFragmentManager, lifecycle, args.resultRecipes)

        TabLayoutMediator(detailsBinding.tabLayout, detailsBinding.recipeViewpager) {
            tab, position -> tab.text = tabTitle[position]
        }.attach()

        addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                appMenuItem = menu.findItem(R.id.save_to_favorites_menu)
                checkIfRecipesSaved(appMenuItem)
            }
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                return menuInflater.inflate(R.menu.details_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    android.R.id.home -> {
                        finish()
                    }

                    R.id.save_to_favorites_menu -> {
                        if (!recipeSaved) {
                            saveToFavourite(menuItem)
                        } else {
                            removeFromFavourite(menuItem)
                        }
                    }

                }
                return true
            }

        }, this, Lifecycle.State.RESUMED)
    }

    private fun checkIfRecipesSaved(menuItem: MenuItem) {
        foodViewModel.readFavouriteRecipes.observe(this) {
            favouriteEntity ->
            try {
                for (savedRecipes in favouriteEntity){
                    if (savedRecipes.result.id == args.resultRecipes.id) {
                        savedRecipeId = savedRecipes.id
                        changeIconColor(menuItem, R.color.yellow)
                        recipeSaved = true
                    }
                }
            } catch (ex: Exception){
                Log.d("DetailsActivity", "Something went wrong !!")
            }

        }
    }

    private fun saveToFavourite(menuItem: MenuItem) {
        val favouriteEntity = FavouriteEntity(
            0,
            args.resultRecipes
        )
        foodViewModel.insertFavouriteRecipes(favouriteEntity)
        changeIconColor(menuItem, R.color.yellow)
        "Recipe Saved".showSnackMessage()
        recipeSaved = true
    }

    private fun removeFromFavourite(menuItem: MenuItem) {
        val favouriteEntity = FavouriteEntity(
            savedRecipeId,
            args.resultRecipes
        )
        foodViewModel.deleteFavouriteRecipes(favouriteEntity)
        changeIconColor(menuItem, R.color.white)
        "Recipe Removed".showSnackMessage()
        recipeSaved = false
    }

    private fun changeIconColor(menuItem: MenuItem, color: Int) {
        menuItem.icon!!.setTint(ContextCompat.getColor(this, color))
    }

    override fun navigateUpTo(upIntent: Intent?): Boolean {
        return super.navigateUpTo(upIntent)
    }

    private fun String.showSnackMessage() {
        Snackbar.make(detailsBinding.detailLayout, this, Snackbar.LENGTH_LONG)
            .setAction("Ok") {}
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        changeIconColor(appMenuItem, R.color.white)
    }
}

