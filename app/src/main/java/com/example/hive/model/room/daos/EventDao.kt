package com.example.hive.model.room.daos

import androidx.room.*
import com.example.hive.model.room.entities.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getAll(): Flow<List<Event>>

    @Query("SELECT * FROM event WHERE id IN (:eventIds)")
    fun loadAllByIds(eventIds: IntArray): Flow<List<Event>>

    @Query("SELECT * FROM event WHERE id = :id LIMIT 1")
    fun findById(id: String): Flow<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg events: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Query("DELETE FROM event")
    suspend fun deleteAll()
}