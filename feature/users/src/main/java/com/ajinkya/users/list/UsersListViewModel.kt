package com.ajinkya.users.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajinkya.data.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersListViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private val refreshError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<UsersListUiState> = combine(
        usersRepository.users,
        isRefreshing,
        refreshError,
    ) { users, refreshing, error ->
        when {
            // Cache not read from disk yet (very brief window at process start).
            users == null -> UsersListUiState.Loading
            // Cached data available: show it immediately, even while refreshing.
            users.isNotEmpty() -> UsersListUiState.Success(users, isRefreshing = refreshing)
            // No cached data yet: show loading while fetching, otherwise the error.
            refreshing -> UsersListUiState.Loading
            error != null -> UsersListUiState.Error(error)
            else -> UsersListUiState.Success(users)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UsersListUiState.Loading,
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            refreshError.value = null
            usersRepository.refreshUsers().onFailure {
                refreshError.value = it.message ?: "Something went wrong"
            }
            isRefreshing.value = false
        }
    }
}

