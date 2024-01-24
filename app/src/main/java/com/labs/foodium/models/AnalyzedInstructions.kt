package com.labs.foodium.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class AnalyzedInstructions(
    @SerializedName("name")
    val name: String,
    @SerializedName("steps")
    val steps: @RawValue List<Steps>
): Parcelable