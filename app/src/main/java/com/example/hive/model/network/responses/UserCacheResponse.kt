package com.example.hive.model.network.responses

import com.google.gson.annotations.SerializedName

data class UserCacheResponse(
    @SerializedName("id")
    var id: String,

    @SerializedName("name")
    var name: String,

    @SerializedName("email")
    var email: String,

    @SerializedName("participation")
    var participation: Int,
)
