package com.example.hive.model.network.responses

import com.google.gson.annotations.SerializedName

data class TopPartnersResponse(
    @SerializedName("top")
    var top: List<String>,
)
