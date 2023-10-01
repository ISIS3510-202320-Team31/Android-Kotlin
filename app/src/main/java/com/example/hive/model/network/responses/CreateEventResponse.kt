package com.example.hive.model.network.responses

data class CreateEventResponse (
    val id: Int,
    val name: String,
    val image: String,
    val place: String,
    val date: String,
    val description: String,
    val num_participants: Int,
    val owner: Int,
    val participants: List<Int>
    )
