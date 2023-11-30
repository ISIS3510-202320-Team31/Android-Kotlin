package com.example.hive.model.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hive.model.room.entities.CategoryChart
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryChartDao {

    @Query("SELECT * FROM category_chart")
    fun getAll(): Flow<List<CategoryChart>>

    @Query("DELETE FROM category_chart")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryChart)
}