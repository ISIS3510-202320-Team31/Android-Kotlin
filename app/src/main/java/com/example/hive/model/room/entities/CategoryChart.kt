package com.example.hive.model.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_chart")
data class CategoryChart(
    @PrimaryKey val category: String,
    @ColumnInfo(name = "value") val value: Float?,
    @ColumnInfo(name = "color") val color: String?,
)
