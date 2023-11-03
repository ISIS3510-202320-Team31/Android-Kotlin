package com.example.hive.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.hive.model.room.daos.EventDao
import com.example.hive.model.room.daos.UserDao
import com.example.hive.model.room.entities.Event
import com.example.hive.model.room.entities.User
import com.example.hive.util.Converters

@Database(entities = [User::class, Event::class], version = 1)
@TypeConverters(Converters::class)
abstract class HiveDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao

    companion object {
        private const val Database_NAME = "hive.db"

        /**
         * As we need only one instance of db in our app will use to store
         * This is to avoid memory leaks in android when there exist multiple instances of db
         */
        @Volatile
        private var INSTANCE: HiveDatabase? = null

        fun getInstance(context: Context): HiveDatabase? {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = androidx.room.Room.databaseBuilder(
                        context.applicationContext,
                        HiveDatabase::class.java,
                        Database_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}