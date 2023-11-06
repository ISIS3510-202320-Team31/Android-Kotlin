package com.example.hive.model.room.daos

import androidx.room.*
import com.example.hive.model.room.entities.EventHistorical
import kotlinx.coroutines.flow.Flow

@Dao
interface EventHistoricalDao {
    @Query("SELECT * FROM event_historical")
    fun getAll(): Flow<List<EventHistorical>>

    @Query("SELECT * FROM event_historical WHERE id IN (:eventIds)")
    fun loadAllByIds(eventIds: IntArray): Flow<List<EventHistorical>>

    @Query("SELECT * FROM event_historical WHERE id = :id LIMIT 1")
    fun findById(id: String): Flow<EventHistorical>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg events: EventHistorical)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventHistorical)

    @Query("DELETE FROM event_historical")
    suspend fun deleteAll()
}