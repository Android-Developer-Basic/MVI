package ru.otus.mvi.statemachine.state

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.common.session.data.Session
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.common.ui.ContentUiIntent
import ru.otus.mvi.statemachine.data.AppDataState

/**
 * Content state
 * - subscribes to session updates
 * - transfers to [LoginFormState] if not authenticated
 * - transfers to [LoggingOutState] if logout requested
 * @param context Common interstate context which does not change
 * @param data Application state data
 * @param sessionManager Session manager
 */
internal class ContentState(
    context: AppContext,
    private val data: AppDataState,
    private val sessionManager: SessionManager
) : BaseAppState(context) {

    /**
     * Called when the state is started
     */
    override fun doStart() {
        super.doStart()
        // Update UI
        setUiState(renderer.renderContentLoading())
        // Subscribe manager
        subscribeSession()
    }

    /**
     * Subscribes session manager
     */
    private fun subscribeSession() = sessionManager.session
        .onEach { session ->
            when(session) {
                is Session.Active -> {
                    Napier.d { "Got active user: ${session.user.username}" }
                    // Update UI
                    setUiState(renderer.renderContent(session.user.username))
                }
                Session.NotLoggedIn -> {
                    Napier.d { "Not logged in. Transferring to login..." }
                    // Update logic
                    setMachineState(factory.loginForm(data))
                }
            }
        }
        // State scope is cleared when state is deactivated
        .launchIn(stateScope)

    override fun doProcess(gesture: AppUiIntent) {
        when(gesture) {
            is AppUiIntent.Content -> when (gesture.content) {
                ContentUiIntent.LogoutClicked -> {
                    Napier.d { "Transferring to logout" }
                    // Update logic
                    setMachineState(factory.loggingOut(data))
                }
            }
            else -> super.doProcess(gesture)
        }
    }
}