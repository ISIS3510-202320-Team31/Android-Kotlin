package com.example.hive.model.network.requests

import com.google.gson.annotations.SerializedName

data class RegisterRequest(

    @SerializedName("login")
    var login: String,

    @SerializedName("name")
    var name: String,

    @SerializedName("career")
    var career: String,

    @SerializedName("birthdate")
    var birthdate: String,

    @SerializedName("email")
    var email: String,

    @SerializedName("password")
    var password: String,

)
