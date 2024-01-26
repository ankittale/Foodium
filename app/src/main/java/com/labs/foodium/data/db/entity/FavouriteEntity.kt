package com.labs.foodium.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.labs.foodium.models.Result
import com.labs.foodium.utils.Constants.Companion.FAVORITE_RECIPES_TABLE

@Entity(tableName = FAVORITE_RECIPES_TABLE)
class FavouriteEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: Result
)