package com.ajinkya.users.detail

import com.ajinkya.model.User

sealed interface UserDetailUiState {
    data object Loading : UserDetailUiState
    data class Success(val user: User) : UserDetailUiState
    data class Error(val message: String) : UserDetailUiState
}