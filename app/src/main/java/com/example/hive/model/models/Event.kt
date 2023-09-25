package com.example.hive.model.models

import com.google.gson.annotations.SerializedName

data class Event
    (
    @SerializedName("id")
    var id: String,

    @SerializedName("image")
    var image: String,

    @SerializedName("name")
    var name: String,

    @SerializedName("description")
    var description: String,

    @SerializedName("date")
    var date: String,

    @SerializedName("place")
    var place: String,

    @SerializedName("num_participants")
    var num_participants: Int,

    @SerializedName("category")
    var category: String,

    @SerializedName("state")
    var state: Boolean,

    @SerializedName("duration")
    var duration: Int,

    @SerializedName("creator_id")
    var creator_id: String,

    @SerializedName("participants")
    var participants: List<Participant>,
)