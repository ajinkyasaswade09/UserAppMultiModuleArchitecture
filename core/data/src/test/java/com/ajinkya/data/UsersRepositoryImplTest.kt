package com.ajinkya.data

import com.ajinkya.data.local.CachedUserEntity
import com.ajinkya.data.local.UsersDao
import com.ajinkya.network.UsersApi
import com.ajinkya.network.model.UserDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UsersRepositoryImplTest {

   private val api: UsersApi = mockk()
   private val dao = FakeUsersDao()

   private fun TestScope.repository() = UsersRepositoryImpl(
       usersApi = api,
       usersDao = dao,
       appScope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
       ioDispatcher = UnconfinedTestDispatcher(testScheduler),
   )

   @Test
   fun `refreshUsers stores mapped users in the cache`() = runTest {
       coEvery { api.getUsers() } returns listOf(
           UserDto(id = 1, name = "Alice", email = "alice@example.com"),
           UserDto(id = 2, name = "Bob"),
       )

       val repository = repository()
       val result = repository.refreshUsers()

       assertTrue(result.isSuccess)
       val users = repository.users.filterNotNull().first()
       assertEquals(2, users.size)
       assertEquals("Alice", users[0].name)
       assertEquals("", users[1].email)
   }

   @Test
   fun `observeUser emits the cached user or null`() = runTest {
       coEvery { api.getUsers() } returns listOf(UserDto(id = 7, name = "Cara"))

       val repository = repository()
       repository.refreshUsers()

       assertEquals("Cara", repository.observeUser(7).first()?.name)
       assertNull(repository.observeUser(999).first())
   }

   @Test
   fun `refreshUsers returns failure and keeps cache when the api throws`() = runTest {
       coEvery { api.getUsers() } returns listOf(UserDto(id = 1, name = "Alice"))
       val repository = repository()
       repository.refreshUsers()

       coEvery { api.getUsers() } throws IOException("network down")
       val result = repository.refreshUsers()

       assertTrue(result.isFailure)
       assertEquals("network down", result.exceptionOrNull()?.message)
       // Previously cached data is untouched on failure.
       assertEquals("Alice", repository.users.filterNotNull().first().first().name)
   }

   private class FakeUsersDao : UsersDao {
       private val users = MutableStateFlow<List<CachedUserEntity>>(emptyList())

       override fun observeUsers(): Flow<List<CachedUserEntity>> = users

       override fun observeUser(id: Int): Flow<CachedUserEntity?> =
           users.map { list -> list.firstOrNull { it.id == id } }

       override suspend fun insertAll(users: List<CachedUserEntity>) {
           this.users.value = this.users.value + users
       }

       override suspend fun clear() {
           users.value = emptyList()
       }
   }
}
