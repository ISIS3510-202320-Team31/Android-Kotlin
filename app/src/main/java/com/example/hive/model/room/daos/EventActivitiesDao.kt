package com.example.hive.model.room.daos

import androidx.room.*
import com.example.hive.model.room.entities.EventActivities
import kotlinx.coroutines.flow.Flow

@Dao
interface EventActivitiesDao {
    @Query("SELECT * FROM event_activities")
    fun getAll(): Flow<List<EventActivities>>

    @Query("SELECT * FROM event_activities WHERE id IN (:eventIds)")
    fun loadAllByIds(eventIds: IntArray): Flow<List<EventActivities>>

    @Query("SELECT * FROM event_activities WHERE id = :id LIMIT 1")
    fun findById(id: String): Flow<EventActivities>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg events: EventActivities)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventActivities)

    @Query("DELETE FROM event_activities")
    suspend fun deleteAll()
}