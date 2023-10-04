package com.example.hive.model.network.requests

import com.google.gson.annotations.SerializedName

data class LoginRequest(

    @SerializedName("login")
    val login: String? = null,

    @SerializedName("password")
    val password: String? = null,

)
