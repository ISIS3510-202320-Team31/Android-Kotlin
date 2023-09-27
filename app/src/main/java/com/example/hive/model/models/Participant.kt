package com.example.hive.model.models

import com.google.gson.annotations.SerializedName

data class Participant(

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

    @SerializedName("verificated")
    var verificated: Boolean,

    @SerializedName("role")
    var role: String,

    @SerializedName("career")
    var career: String,

    @SerializedName("birthdate")
    var birthdate: String,
)
