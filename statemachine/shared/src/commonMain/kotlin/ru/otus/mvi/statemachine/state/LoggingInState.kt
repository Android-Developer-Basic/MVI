package ru.otus.mvi.statemachine.state

import io.github.aakira.napier.Napier
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import ru.otus.mvi.common.data.exception.toAppException
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.statemachine.data.AppDataState

/**
 * Runs login operation
 */
internal class LoggingInState(
    context: AppContext,
    val dataState: AppDataState,
    val sessionManager: SessionManager
) : BaseAppState(context) {
    /**
     * Called when the state is started
     */
    override fun doStart() {
        super.doStart()
        setUiState(renderer.renderLoggingIn(dataState.login))
        login()
    }

    private fun login() = stateScope.launch {
        Napier.d { "Logging in..." }
        val login = dataState.login
        try {
            val session = sessionManager.login(login.username, login.password)
            Napier.d { "Logged in user: ${session.user.username}" }
            // Update logic
            setMachineState(factory.content(dataState))
        } catch (e: Throwable) {
            ensureActive()
            Napier.w(e) { "Login error" }
            // Update logic
            setMachineState(factory.loginError(dataState, e.toAppException()))
        }
    }
}