package com.example.hive.model.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topCreators")
data class TopCreators(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "average") val average: Float?,
)
