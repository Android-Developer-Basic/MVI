package ru.otus.mvi.common.ui

/**
 * Login flow UI state
 */
sealed class LoginUiState {
    /**
     * Login form
     * @property username Username value
     * @property password Password value
     * @property loginEnabled Can we process with login
     */
    data class Form(val username: String, val password: String, val loginEnabled: Boolean) : LoginUiState()

    /**
     * Login in progress
     * @property username Username value
     * @property password Password value
     */
    data class LoggingIn(val username: String, val password: String) : LoginUiState()

    /**
     * Login error
     * @property message Error message
     * @property retryAvailable Is retry available
     */
    data class Error(val message: String, val retryAvailable: Boolean) : LoginUiState()
}