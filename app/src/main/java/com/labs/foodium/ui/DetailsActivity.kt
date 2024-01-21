package com.labs.foodium.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.labs.foodium.R
import com.labs.foodium.databinding.ActivityDetailsBinding
import com.labs.foodium.adapter.ViewPagerFragmentAdapter

class DetailsActivity: AppCompatActivity() {

    private val args by navArgs<DetailsActivityArgs>()
    private lateinit var detailsBinding: ActivityDetailsBinding

    private val tabTitle = arrayOf("Overview", "Ingredients", "Instructions")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details)
        setSupportActionBar(detailsBinding.detailToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        detailsBinding.recipeViewpager.adapter = ViewPagerFragmentAdapter(supportFragmentManager, lifecycle, args.resultRecipes)

        TabLayoutMediator(detailsBinding.tabLayout, detailsBinding.recipeViewpager) {
            tab, position -> tab.text = tabTitle[position]
        }.attach()

    }

    override fun navigateUpTo(upIntent: Intent?): Boolean {
        return super.navigateUpTo(upIntent)
    }
}