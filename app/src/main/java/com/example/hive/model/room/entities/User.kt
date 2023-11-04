package com.example.hive.model.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "icon") val icon: String?,
    @ColumnInfo(name = "login") val login: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "verificated") val verificated: Boolean?,
    @ColumnInfo(name = "role") val role: String?,
    @ColumnInfo(name = "career") val career: String?,
    @ColumnInfo(name = "birthdate") val birthdate: String?,
)
