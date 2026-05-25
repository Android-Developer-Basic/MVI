package ru.otus.mvi.common.ui

/**
 * UI state of logout flow
 */
sealed class LogoutUiState {
    /**
     * Logging out user
     */
    data object LoggingOut : LogoutUiState()
}