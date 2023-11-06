package com.example.hive.model.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_historical")
data class EventHistorical(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "image") val image: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "place") val place: String?,
    @ColumnInfo(name = "num_participants") val num_participants: Int?,
    @ColumnInfo(name = "category") val category: String?,
    @ColumnInfo(name = "state") val state: Boolean?,
    @ColumnInfo(name = "duration") val duration: Int?,
    @ColumnInfo(name = "creator_id") val creator_id: String?,
    @ColumnInfo(name = "creator") val creator: String?,
    @ColumnInfo(name = "participants") val participants: List<String>?,
    @ColumnInfo(name = "links") val links: List<String>?,
)


