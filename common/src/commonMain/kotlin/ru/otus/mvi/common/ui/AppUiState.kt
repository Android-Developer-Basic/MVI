package ru.otus.mvi.common.ui

/**
 * Complete set of possible UI states
 */
sealed class AppUiState {
    /**
     * Content screen
     * @property content Child UI state
     */
    data class Content(val content: ContentUiState) : AppUiState()

    /**
     * Login screen
     * @property login Child UI state
     */
    data class Login(val login: LoginUiState) : AppUiState()

    /**
     * Logout screen
     * @property logout Child UI state
     */
    data class Logout(val logout: LogoutUiState): AppUiState()
}