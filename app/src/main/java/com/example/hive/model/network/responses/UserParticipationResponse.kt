package com.example.hive.model.network.responses
import com.google.gson.annotations.SerializedName

data class UserParticipationResponse(

    @SerializedName("size")
    var size: Int
)
