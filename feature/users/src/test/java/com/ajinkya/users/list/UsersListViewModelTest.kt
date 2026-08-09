package com.ajinkya.users.list

import app.cash.turbine.test
import com.ajinkya.data.UsersRepository
import com.ajinkya.model.User
import com.ajinkya.users.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class UsersListViewModelTest {

   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()

   @Test
   fun `emits Loading then Success with freshly fetched users`() = runTest {
       val users = listOf(sampleUser(1), sampleUser(2))
       val viewModel = UsersListViewModel(
           FakeUsersRepository(refreshResult = Result.success(Unit), usersOnRefresh = users),
       )

       viewModel.uiState.test {
           assertEquals(UsersListUiState.Loading, awaitItem())
           var item = awaitItem()
           while (item is UsersListUiState.Success && item.isRefreshing) {
               item = awaitItem()
           }
           assertEquals(UsersListUiState.Success(users, isRefreshing = false), item)
           cancelAndConsumeRemainingEvents()
       }
   }

   @Test
   fun `shows cached users even when refresh fails`() = runTest {
       val cached = listOf(sampleUser(1), sampleUser(2))
       val viewModel = UsersListViewModel(
           FakeUsersRepository(
               refreshResult = Result.failure(RuntimeException("offline")),
               initialUsers = cached,
           ),
       )

       viewModel.uiState.test {
           // Previously cached data is shown; a failed refresh must not surface an error.
           var item = awaitItem()
           while (item !is UsersListUiState.Success || item.isRefreshing) {
               assertTrue(item !is UsersListUiState.Error)
               item = awaitItem()
           }
           assertEquals(UsersListUiState.Success(cached, isRefreshing = false), item)
           cancelAndConsumeRemainingEvents()
       }
   }

   @Test
   fun `emits Loading then Error when refresh fails and there is no cache`() = runTest {
       val viewModel = UsersListViewModel(
           FakeUsersRepository(refreshResult = Result.failure(RuntimeException("boom"))),
       )

       viewModel.uiState.test {
           assertEquals(UsersListUiState.Loading, awaitItem())
           var item = awaitItem()
           while (item is UsersListUiState.Loading) {
               item = awaitItem()
           }
           assertTrue(item is UsersListUiState.Error)
           assertEquals("boom", (item as UsersListUiState.Error).message)
           cancelAndConsumeRemainingEvents()
       }
   }

   private fun sampleUser(id: Int) = User(
       id = id,
       name = "User $id",
       company = "Company",
       username = "user$id",
       email = "user$id@example.com",
       address = "Address",
       zip = "00000",
       state = "State",
       country = "Country",
       phone = "123",
       photo = "https://example.com/$id.png",
   )

   private class FakeUsersRepository(
       private val refreshResult: Result<Unit>,
       private val usersOnRefresh: List<User> = emptyList(),
       initialUsers: List<User> = emptyList(),
   ) : UsersRepository {

       private val usersFlow = MutableStateFlow<List<User>?>(initialUsers)

       override val users: StateFlow<List<User>?> = usersFlow

       override fun observeUser(id: Int): Flow<User?> =
           usersFlow.map { list -> list?.firstOrNull { it.id == id } }

       override suspend fun refreshUsers(): Result<Unit> =
           refreshResult.onSuccess { usersFlow.value = usersOnRefresh }
   }
}
