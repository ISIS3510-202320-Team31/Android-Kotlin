package com.example.hive.model.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "top_partners")
data class TopPartners(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "top") val top: List<String>?,
)
