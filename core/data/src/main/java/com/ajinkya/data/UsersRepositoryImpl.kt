package com.ajinkya.data

import com.ajinkya.data.di.ApplicationScope
import com.ajinkya.data.di.IoDispatcher
import com.ajinkya.data.local.UsersDao
import com.ajinkya.data.local.toDomain
import com.ajinkya.data.local.toEntity
import com.ajinkya.model.User
import com.ajinkya.network.UsersApi
import com.ajinkya.network.model.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepositoryImpl @Inject constructor(
    private val usersApi: UsersApi,
    private val usersDao: UsersDao,
    @param:ApplicationScope private val appScope: CoroutineScope,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : UsersRepository {

    // Eagerly reads the cache from disk at app start and keeps it hot for the whole
    // process, so the previously stored data is ready to show immediately on launch.
    override val users: StateFlow<List<User>?> =
        usersDao.observeUsers()
            .map { entities -> entities.map { it.toDomain() } }
            .stateIn(
                scope = appScope,
                started = SharingStarted.Eagerly,
                initialValue = null,
            )

    override fun observeUser(id: Int): Flow<User?> =
        usersDao.observeUser(id).map { it?.toDomain() }

    override suspend fun refreshUsers(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val fetched = usersApi.getUsers().map { it.toDomain() }
            usersDao.replaceAll(fetched.map { it.toEntity() })
        }
    }
}
