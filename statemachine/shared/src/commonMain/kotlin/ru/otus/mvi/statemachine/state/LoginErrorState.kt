package ru.otus.mvi.statemachine.state

import io.github.aakira.napier.Napier
import ru.otus.mvi.common.data.exception.AppException
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.common.ui.LoginUiIntent
import ru.otus.mvi.statemachine.data.AppDataState

/**
 * Displays login error and proceeds with retry logic
 */
internal class LoginErrorState(
    context: AppContext,
    private val dataState: AppDataState,
    private val error: AppException
) : BaseAppState(context) {

    private val canRetry: Boolean get() = error.isFatal.not()

    /**
     * Called when the state is started
     */
    override fun doStart() {
        super.doStart()
        setUiState(renderer.renderLoginError(
            message = error.message,
            canRetry = canRetry
        ))
    }

    /**
     * UI-intent dispatch
     * Take only those intents we like to process
     */
    override fun doProcess(gesture: AppUiIntent) {
        super.doProcess(gesture)
        when(gesture) {
            is AppUiIntent.Login -> when(val loginGesture = gesture.login) {
                is LoginUiIntent.RetryErrorClicked -> {
                    if (canRetry) {
                        Napier.d { "Retrying error" }
                        // Update logic
                        setMachineState(factory.loggingIn(dataState))
                    }
                }
                is LoginUiIntent.DismissErrorClicked -> {
                    Napier.d { "Fatal error. Returning to login form" }
                    // Update logic
                    setMachineState(factory.loginForm(dataState))
                }
                else -> super.doProcess(gesture)
            }
            else -> super.doProcess(gesture)
        }
    }
}