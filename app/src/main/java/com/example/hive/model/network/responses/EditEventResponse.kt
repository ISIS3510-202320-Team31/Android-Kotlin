package com.example.hive.model.network.responses

data class EditEventResponse (
    val id: String,
    val name: String,
    val image: String,
    val place: String,
    val date: String,
    val description: String,
    val category: String,
    val state: Boolean,
    val duration: Int,
    val num_participants: Int,
    val creador: String
    )
