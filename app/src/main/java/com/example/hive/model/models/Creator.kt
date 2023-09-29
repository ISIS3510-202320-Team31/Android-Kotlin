package com.example.hive.model.models

import com.google.gson.annotations.SerializedName

data class Creator(

    @SerializedName("id")
    var id: String,

    @SerializedName("icon")
    var icon: String,

    @SerializedName("login")
    var login: String,

    @SerializedName("name")
    var name: String,

    @SerializedName("password")
    var password: String,

    @SerializedName("email")
    var email: String,

    @SerializedName("verified")
    var verified: Boolean,

    @SerializedName("role")
    var role: String,

    @SerializedName("career")
    var career: String,

    @SerializedName("birthday")
    var birthday: String,
)
