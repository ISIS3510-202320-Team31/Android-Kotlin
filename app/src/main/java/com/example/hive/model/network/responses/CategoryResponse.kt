package com.example.hive.model.network.responses

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("category")
    var category: String,

    @SerializedName("value")
    var value: Float,

    @SerializedName("color")
    var color: String,
)
