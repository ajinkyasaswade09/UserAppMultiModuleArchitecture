package com.ajinkya.users.list

import com.ajinkya.model.User

sealed interface UsersListUiState {
    data object Loading : UsersListUiState

    /**
     * Shows the users currently held in the cache. [isRefreshing] is true while a fresh
     * fetch is in flight so the UI can show cached data with a subtle refresh indicator.
     */
    data class Success(
        val users: List<User>,
        val isRefreshing: Boolean = false,
    ) : UsersListUiState

    data class Error(val message: String) : UsersListUiState
}
