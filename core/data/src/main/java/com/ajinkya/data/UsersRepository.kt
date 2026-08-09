package com.ajinkya.data

import com.ajinkya.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UsersRepository {

    /**
     * The users held in the persistent cache, kept warm for the whole app lifetime.
     *
     * - `null` means the cache has not been read from disk yet (a very brief window at
     *   process start).
     * - A list (even empty) means the cache has been read; it reflects the previously
     *   stored data immediately on launch and updates automatically after [refreshUsers].
     */
    val users: StateFlow<List<User>?>

    /** Streams a single cached user by id, or null while it is not in the cache. */
    fun observeUser(id: Int): Flow<User?>

    /**
     * Fetches the latest users from the server and, on success, atomically replaces the
     * persistent cache. Observers of [users]/[observeUser] receive the fresh data
     * automatically.
     */
    suspend fun refreshUsers(): Result<Unit>
}
