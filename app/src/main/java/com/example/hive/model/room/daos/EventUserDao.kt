package com.example.hive.model.room.daos
import androidx.room.*
import com.example.hive.model.room.entities.EventUser
import kotlinx.coroutines.flow.Flow

@Dao
interface EventUserDao {
    @Query("SELECT * FROM event_user")
    fun getAll(): Flow<List<EventUser>>

    @Query("SELECT * FROM event_historical WHERE id IN (:eventIds)")
    fun loadAllByIds(eventIds: IntArray): Flow<List<EventUser>>

    @Query("SELECT * FROM event_historical WHERE id = :id LIMIT 1")
    fun findById(id: String): Flow<EventUser>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg events: EventUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventUser)

    @Query("DELETE FROM event_historical")
    suspend fun deleteAll()
}