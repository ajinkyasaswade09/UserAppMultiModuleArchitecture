package com.ajinkya.users.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajinkya.data.UsersRepository
import com.ajinkya.users.navigation.UsersDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val userId: Int = savedStateHandle.get<Int>(UsersDestinations.USER_ID_ARG) ?: -1

    private val isRefreshing = MutableStateFlow(false)
    private val refreshError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<UserDetailUiState> = combine(
        usersRepository.observeUser(userId),
        isRefreshing,
        refreshError,
    ) { user, refreshing, error ->
        when {
            user != null -> UserDetailUiState.Success(user)
            refreshing -> UserDetailUiState.Loading
            error != null -> UserDetailUiState.Error(error)
            else -> UserDetailUiState.Error("User not found")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserDetailUiState.Loading,
    )

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            // Only hit the network if the user is not already in the persistent cache.
            if (usersRepository.observeUser(userId).first() == null) {
                refresh()
            }
        }
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