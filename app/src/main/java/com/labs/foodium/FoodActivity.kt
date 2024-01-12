package com.labs.foodium

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.labs.foodium.databinding.ActivityFoodBinding

class FoodActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var activityFoodBinding: ActivityFoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFoodBinding = DataBindingUtil.setContentView(this, R.layout.activity_food)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        setSupportActionBar(activityFoodBinding.foodToolBar)
        navController = navHostFragment.findNavController()

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.recipesFragment,
                R.id.favouriteRecipeFragment,
                R.id.foodJokeFragment
            )
        )
        activityFoodBinding.bottomAppBar.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}