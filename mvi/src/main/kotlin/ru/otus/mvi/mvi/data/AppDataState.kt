package ru.otus.mvi.mvi.data

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import ru.otus.mvi.common.data.exception.AppException
import ru.otus.mvi.common.data.exception.toAppException
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.common.session.data.Session
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.common.ui.AppUiState
import ru.otus.mvi.common.ui.ContentUiIntent
import ru.otus.mvi.common.ui.ContentUiState
import ru.otus.mvi.common.ui.LoginUiIntent
import ru.otus.mvi.common.ui.LogoutUiState

/**
 * Complete application state
 */
data class AppDataState(
    val appState: AppState = AppState.CONTENT,
    val session: Session = Session.NotLoggedIn,
    val login: LoginDataState = LoginDataState()
)

/**
 * Application state
 */
enum class AppState {
    CONTENT,
    LOGIN,
    LOGOUT
}

/**
 * Renders data state to UI state
 */
fun AppDataState.render(): AppUiState = when(appState) {
    AppState.CONTENT -> AppUiState.Content(
        when(session) {
            is Session.Active -> {
                ContentUiState.Content(
                    username = session.user.username,
                    logoutEnabled = true
                )
            }
            Session.NotLoggedIn -> ContentUiState.Loading
        }
    )
    AppState.LOGIN -> AppUiState.Login(
        login.render()
    )
    AppState.LOGOUT -> AppUiState.Logout(
        LogoutUiState.LoggingOut
    )
}

/**
 * State reducing intents
 */
sealed class AppIntent {
    /**
     * UI-gesture
     */
    data class UiUpdate(val ui: AppUiIntent) : AppIntent()

    /**
     * Intents from business logic
     */
    sealed class SessionIntent : AppIntent() {
        /**
         * Logged in
         */
        data class LoggedIn(val session: Session.Active) : SessionIntent()

        /**
         * Login error
         */
        data class LoginFailed(val error: AppException) : SessionIntent()

        /**
         * Logged out
         */
        data object LoggedOut : SessionIntent()
    }
}

/**
 * Reducer context - a set of common instruments
 */
interface ReducerContext {
    /**
     * Coroutine scope
     */
    val scope: CoroutineScope

    /**
     * Session manager
     */
    val sessionManager: SessionManager

    /**
     * Runs state reducer
     */
    fun reduce(intent: AppIntent)
}

/**
 * Reduces session change update
 */
fun AppDataState.reduceSession(intent: AppIntent.SessionIntent): AppDataState = when(intent) {

    is AppIntent.SessionIntent.LoggedIn -> {
        Napier.d { "Session state change: Logged in user ${intent.session.user.username}" }
        copy(
            appState = AppState.CONTENT,
            session = intent.session,
            login = login.copy(
                password = "",
                isRunning = false,
                error = null
            )
        )
    }

    AppIntent.SessionIntent.LoggedOut -> {
        Napier.d { "Session state change: Logged out" }
        copy(
            appState = AppState.LOGIN,
            session = Session.NotLoggedIn,
            login = login.copy(
                password = "",
                isRunning = false,
                error = null
            )
        )
    }

    is AppIntent.SessionIntent.LoginFailed -> {
        Napier.w(intent.error) { "Session state change: Login failed: " }
        copy(
            appState = AppState.LOGIN,
            session = Session.NotLoggedIn,
            login = login.copy(
                isRunning = false,
                error = intent.error
            )
        )
    }
}

/**
 * Reduces content state updates
 */
context(reducer: ReducerContext)
fun AppDataState.reduceContent(intent: ContentUiIntent): AppDataState = when {

    AppState.CONTENT != appState -> this

    intent is ContentUiIntent.LogoutClicked -> {
        Napier.d("Logging out...")
        reducer.scope.launch {
            reducer.sessionManager.logout()
            reducer.reduce(AppIntent.SessionIntent.LoggedOut)
        }
        copy(appState = AppState.LOGOUT)
    }

    else -> this
}

/**
 * Reduces content state updates
 */
context(reducer: ReducerContext)
fun AppDataState.reduceLogin(intent: LoginUiIntent): AppDataState {
    fun login() {
        Napier.d { "Logging in user: ${login.username}" }
        reducer.scope.launch {
            try {
                val newSession = reducer.sessionManager.login(login.username, login.password)
                reducer.reduce(AppIntent.SessionIntent.LoggedIn(newSession))
            } catch (e: Throwable) {
                ensureActive()
                reducer.reduce(AppIntent.SessionIntent.LoginFailed(e.toAppException()))
            }
        }
    }

    return when {
        AppState.LOGIN != appState -> this
        else -> when (intent) {
            is LoginUiIntent.UsernameChanged -> {
                copy(login = login.copy(username = intent.value))
            }

            is LoginUiIntent.PasswordChanged -> {
                copy(login = login.copy(password = intent.value))
            }

            LoginUiIntent.LoginClicked -> {
                Napier.d { "Logging in..." }
                when {
                    login.hasValidForm() -> {
                        login()
                        copy(login = login.copy(isRunning = true, error = null))
                    }
                    else -> this
                }
            }

            LoginUiIntent.RetryErrorClicked -> {
                Napier.d { "Retrying..." }
                login()
                copy(login = login.copy(isRunning = true, error = null))
            }

            LoginUiIntent.DismissErrorClicked -> {
                Napier.d { "Dismissing error..." }
                copy(login = login.copy(error = null))
            }
        }
    }
}