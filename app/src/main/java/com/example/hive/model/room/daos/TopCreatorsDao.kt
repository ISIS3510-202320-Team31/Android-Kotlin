package com.example.hive.model.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hive.model.room.entities.TopCreators
import com.example.hive.model.room.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface TopCreatorsDao {
    @Query("SELECT * FROM topCreators")
    fun getAll(): Flow<List<TopCreators>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg users: TopCreators)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: TopCreators)

    @Query("DELETE FROM topCreators")
    suspend fun deleteAll()

}