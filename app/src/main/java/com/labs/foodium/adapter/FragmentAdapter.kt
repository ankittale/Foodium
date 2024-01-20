package com.labs.foodium.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.labs.foodium.ui.fragment.IngredientsFragment
import com.labs.foodium.ui.fragment.InstructionsFragment
import com.labs.foodium.ui.fragment.OverviewFragment


class ViewPagerFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val arrayList: ArrayList<Fragment> = ArrayList()

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OverviewFragment()
            1 -> IngredientsFragment()
            2 -> InstructionsFragment()
            else -> OverviewFragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}