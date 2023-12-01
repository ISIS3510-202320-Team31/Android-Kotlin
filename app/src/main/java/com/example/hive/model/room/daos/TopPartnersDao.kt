package com.example.hive.model.room.daos

import androidx.room.*
import com.example.hive.model.room.entities.TopPartners
import kotlinx.coroutines.flow.Flow

@Dao
interface TopPartnersDao {

    @Query("SELECT * FROM top_partners")
    fun getAll(): Flow<List<TopPartners>>

    @Query("DELETE FROM top_partners")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(topPartners: TopPartners)
}