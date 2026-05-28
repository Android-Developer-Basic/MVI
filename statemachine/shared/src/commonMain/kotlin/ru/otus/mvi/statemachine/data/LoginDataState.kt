package ru.otus.mvi.statemachine.data

/**
 * Login flow data state
 * @property username Current username
 * @property password Current password
 * @property error Login error if any
 */
data class LoginDataState(
    val username: String = "",
    val password: String = ""
)

/**
 * Checks if login form is valid
 */
fun LoginDataState.hasValidForm() = username.isNotBlank() && password.isNotBlank()