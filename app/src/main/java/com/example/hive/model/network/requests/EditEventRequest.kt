package com.example.hive.model.network.requests

import com.google.gson.annotations.SerializedName

data class EditEventRequest (

    @SerializedName("name")
    var name: String,

    @SerializedName("place")
    var place: String,

    @SerializedName("date")
    var date: String,

    @SerializedName("description")
    var description: String,

    @SerializedName("num_participants")
    var num_participants: Int,

    @SerializedName("category")
    var category: String,

    @SerializedName("state")
    var state: Boolean,

    @SerializedName("duration")
    var duration: Int,

    @SerializedName("creator")
    var creador: String,

    @SerializedName("tags")
    var tags: List<String>,

    @SerializedName("links")
    var links: List<String>


    )