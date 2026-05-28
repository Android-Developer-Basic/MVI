package ru.otus.mvi.statemachine.state

import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.statemachine.data.AppDataState

/**
 * Logs user out
 */
internal class LoggingOutState(
    context: AppContext,
    private val dataState: AppDataState,
    private val sessionManager: SessionManager
) : BaseAppState(context) {

    /**
     * Called when the state is started
     */
    override fun doStart() {
        super.doStart()
        setUiState(renderer.renderLoggingOut())
        logOut()
    }

    private fun logOut() = stateScope.launch {
        Napier.d { "Logging out..." }
        sessionManager.logout()
        // Update logic
        setMachineState(
            factory.content(
                // Drop password
                dataState.copy(
                    login = dataState.login.copy(
                        password = ""
                    )
                )
            )
        )
    }
}