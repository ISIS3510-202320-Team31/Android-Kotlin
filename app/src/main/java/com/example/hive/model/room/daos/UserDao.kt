package com.example.hive.model.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hive.model.room.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): Flow<List<User>>

    @Query("SELECT * FROM user WHERE email LIKE :email LIMIT 1")
    fun findByEmail(email: String): Flow<User>

    @Query("SELECT * FROM user WHERE id LIKE :id LIMIT 1")
    fun findById(id: String): Flow<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("DELETE FROM user")
    fun deleteAll()

}