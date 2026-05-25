package ru.otus.mvi.uistate.ui.login

import ru.otus.mvi.common.data.exception.AppException
import ru.otus.mvi.common.ui.LoginUiState

/**
 * Login flow data state
 * @property username Current username
 * @property password Current password
 * @property isRunning Login operation status
 * @property error Login error if any
 */
data class LoginDataState(
    val username: String,
    val password: String,
    val isRunning: Boolean = false,
    val error: AppException? = null
)

/**
 * Checks if login form is valid
 */
fun LoginDataState.hasValidForm() = username.isNotBlank() && password.isNotBlank()

/**
 * Renders current data state
 */
fun LoginDataState.render(): LoginUiState = when {
    isRunning -> LoginUiState.LoggingIn(username, password)
    null != error -> LoginUiState.Error(error.message, error.isFatal.not())
    else -> LoginUiState.Form(username, password, hasValidForm())
}
