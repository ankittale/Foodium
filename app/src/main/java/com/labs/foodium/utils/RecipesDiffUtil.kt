package com.labs.foodium.utils

import androidx.recyclerview.widget.DiffUtil
import com.labs.foodium.models.Result

//Code to compare list and update only changed view

class RecipesDiffUtil(
    private val oldList: List<Result>, private val newList: List<Result> ) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}