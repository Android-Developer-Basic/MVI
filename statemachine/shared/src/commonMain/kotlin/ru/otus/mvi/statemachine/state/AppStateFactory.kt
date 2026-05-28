package ru.otus.mvi.statemachine.state

import io.github.aakira.napier.Napier
import ru.otus.mvi.common.data.exception.AppException
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.statemachine.data.AppDataState
import ru.otus.mvi.statemachine.ui.AppUiRenderer

/**
 * Application state factory
 */
internal interface AppStateFactory {
    /**
     * Content state
     * @param data Data to pass between states
     */
    fun content(data: AppDataState): AppState

    /**
     * Login form state
     * @param data Data to pass between states
     */
    fun loginForm(data: AppDataState): AppState

    /**
     * Logging-in state
     * @param data Data to pass between states
     */
    fun loggingIn(data: AppDataState): AppState

    /**
     * Logging-in error
     * @param data Data to pass between states
     * @param error Error - something that is passed between two states only
     */
    fun loginError(data: AppDataState, error: AppException): AppState

    /**
     * Logging-out
     * @param data Data to pass between states
     */
    fun loggingOut(data: AppDataState): AppState

    /**
     * State factory implementation
     */
    class Impl(private val sessionManager: SessionManager, renderer: AppUiRenderer) : AppStateFactory {

        /**
         * Common context
         */
        private val context = object : AppContext {
            override val factory: AppStateFactory = this@Impl
            override val renderer: AppUiRenderer = renderer
        }

        override fun content(data: AppDataState): AppState {
            Napier.d { "Creating state: ContentState" }
            return ContentState(context, data, sessionManager)
        }

        override fun loginForm(data: AppDataState): AppState {
            Napier.d { "Creating state: LoginFormState" }
            return LoginFormState(context, data)
        }

        override fun loggingIn(data: AppDataState): AppState {
            Napier.d { "Creating state: LoggingInState" }
            return LoggingInState(context, data, sessionManager)
        }

        override fun loginError(data: AppDataState, error: AppException): AppState {
            Napier.d { "Creating state: LoginErrorState" }
            return LoginErrorState(context, data, error)
        }

        override fun loggingOut(data: AppDataState): AppState {
            Napier.d { "Creating state: LoggingOutState" }
            return LoggingOutState(context, data, sessionManager)
        }
    }
}