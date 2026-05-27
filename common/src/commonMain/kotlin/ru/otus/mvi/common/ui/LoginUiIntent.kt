package ru.otus.mvi.common.ui

/**
 * UI gestures for login state
 */
sealed class LoginUiIntent {
    /**
     * User changed username
     * @property value Username value
     */
    data class UsernameChanged(val value: String) : LoginUiIntent()

    /**
     * User changed password
     * @property value Password value
     */
    data class PasswordChanged(val value: String) : LoginUiIntent()

    /**
     * User clicked Login button
     */
    data object LoginClicked : LoginUiIntent()

    /**
     * User clicked "Dismiss error"
     */
    data object DismissErrorClicked : LoginUiIntent()

    /**
     * User clicked "Retry error"
     */
    data object RetryErrorClicked : LoginUiIntent()
}