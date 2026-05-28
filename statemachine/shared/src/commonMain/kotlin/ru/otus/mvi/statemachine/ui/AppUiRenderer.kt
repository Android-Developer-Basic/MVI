package ru.otus.mvi.statemachine.ui

import ru.otus.mvi.common.ui.AppUiState
import ru.otus.mvi.common.ui.ContentUiState
import ru.otus.mvi.common.ui.LoginUiState
import ru.otus.mvi.common.ui.LogoutUiState
import ru.otus.mvi.statemachine.data.LoginDataState
import ru.otus.mvi.statemachine.data.hasValidForm

/**
 * Renders screens
 */
internal interface AppUiRenderer {
    /**
     * Renders loading
     */
    fun renderContentLoading(): AppUiState

    /**
     * Renders content
     * @param username Logged-in user
     */
    fun renderContent(username: String): AppUiState

    /**
     * Renders login form
     * @param loginDataState Login data
     */
    fun renderLogin(loginDataState: LoginDataState): AppUiState

    /**
     * Renders login-loading
     * @param loginDataState Login data
     */
    fun renderLoggingIn(loginDataState: LoginDataState): AppUiState

    /**
     * Renders login-error
     * @param message Error message
     * @param canRetry Retry available
     */
    fun renderLoginError(message: String, canRetry: Boolean): AppUiState

    /**
     * Renders logging-out
     */
    fun renderLoggingOut(): AppUiState

    /**
     * Renderer implementation
     */
    class Impl : AppUiRenderer {
        override fun renderContentLoading(): AppUiState = AppUiState.Content(ContentUiState.Loading)

        override fun renderContent(username: String): AppUiState = AppUiState.Content(
            ContentUiState.Content(username, true)
        )

        override fun renderLogin(loginDataState: LoginDataState): AppUiState = AppUiState.Login(
            LoginUiState.Form(
                loginDataState.username,
                loginDataState.password,
                loginDataState.hasValidForm()
            )
        )

        override fun renderLoggingIn(loginDataState: LoginDataState): AppUiState = AppUiState.Login(
            LoginUiState.LoggingIn(loginDataState.username, loginDataState.password)
        )

        override fun renderLoginError(message: String, canRetry: Boolean): AppUiState = AppUiState.Login(
            LoginUiState.Error(message, canRetry)
        )

        override fun renderLoggingOut(): AppUiState = AppUiState.Logout(
            LogoutUiState.LoggingOut
        )
    }
}