package com.ajinkya.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CachedUserEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class UserAppDatabase : RoomDatabase() {
    abstract fun usersDao(): UsersDao

    companion object {
        const val DATABASE_NAME = "userapp.db"
    }
}
