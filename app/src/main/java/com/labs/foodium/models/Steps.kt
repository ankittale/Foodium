package com.labs.foodium.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Steps (
    @SerializedName("number")
    val number: Long,
    @SerializedName("step")
    val step: String,
    @SerializedName("ingredients")
    val ingredients: @RawValue List<Ent>,
    @SerializedName("equipment")
    val equipment: List<Ent>,
    @SerializedName("length")
    val length: @RawValue Length? = null
): Parcelable

@Parcelize
data class Ent(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("localizedName")
    val localizedName: String,
    @SerializedName("image")
    val image: String
): Parcelable

@Parcelize
data class Length(
    @SerializedName("number")
    val number: Long,
    @SerializedName("unit")
    val unit: String
): Parcelable