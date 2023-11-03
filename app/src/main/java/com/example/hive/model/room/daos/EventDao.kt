package com.example.hive.model.room.daos

import androidx.room.*
import com.example.hive.model.room.entities.Event

@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getAll(): List<Event>

    @Query("SELECT * FROM event WHERE id IN (:eventIds)")
    fun loadAllByIds(eventIds: IntArray): List<Event>

    @Query("SELECT * FROM event WHERE id = :id LIMIT 1")
    fun findById(id: String): Event

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg events: Event)

    @Query("DELETE FROM event")
    fun delete(event: Event)

    @Query("DELETE FROM event")
    fun deleteAll()
}