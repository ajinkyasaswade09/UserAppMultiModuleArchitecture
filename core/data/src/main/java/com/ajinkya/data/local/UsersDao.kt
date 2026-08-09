package com.ajinkya.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {

    /** Observes the cached users. Emits again automatically whenever the cache changes. */
    @Query("SELECT * FROM cached_users ORDER BY id")
    fun observeUsers(): Flow<List<CachedUserEntity>>

    /** Observes a single cached user, or null if it is not present. */
    @Query("SELECT * FROM cached_users WHERE id = :id")
    fun observeUser(id: Int): Flow<CachedUserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<CachedUserEntity>)

    @Query("DELETE FROM cached_users")
    suspend fun clear()

    /**
     * Atomically replaces the whole cache with a freshly fetched list so the UI never
     * observes a half-written state.
     */
    @Transaction
    suspend fun replaceAll(users: List<CachedUserEntity>) {
        clear()
        insertAll(users)
    }
}

