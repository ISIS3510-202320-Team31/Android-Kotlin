package com.example.hive.model.network.responses

import com.google.gson.annotations.SerializedName

data class TopCreatorsResponse (
    @SerializedName("name")
    var name: String,

    @SerializedName("average")
    var average: Float,
)