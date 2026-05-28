package ru.otus.mvi.statemachine.state

import io.github.aakira.napier.Napier
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.common.ui.LoginUiIntent
import ru.otus.mvi.statemachine.data.AppDataState
import ru.otus.mvi.statemachine.data.LoginDataState
import ru.otus.mvi.statemachine.data.hasValidForm
import kotlin.properties.Delegates

internal class LoginFormState(
    context: AppContext,
    dataState: AppDataState
) : BaseAppState(context) {

    /**
     * Data state
     * Renders with every change
     */
    private var dataState: AppDataState by Delegates.observable(dataState) {_, _, newValue ->
        render(newValue.login)
    }

    /**
     * "Lens" to login state
     * @param block Something to do with login data
     */
    private inline fun updateLogin(block: LoginDataState.() -> LoginDataState) {
        dataState = dataState.copy(login = dataState.login.block())
    }

    /**
     * Called when the state is started
     */
    override fun doStart() {
        super.doStart()
        render(dataState.login)
    }

    /**
     * UI-intent dispatch
     * Take only those intents we like to process
     */
    override fun doProcess(gesture: AppUiIntent) {
        when(gesture) {
            is AppUiIntent.Login -> when(val loginGesture = gesture.login) {
                is LoginUiIntent.UsernameChanged -> updateLogin {
                    copy(username = loginGesture.value)
                }
                is LoginUiIntent.PasswordChanged -> updateLogin {
                    copy(password = loginGesture.value)
                }
                LoginUiIntent.LoginClicked -> {
                    dataState.login.run {
                        if (hasValidForm()) {
                            Napier.d { "Transferring to logging-in..." }
                            // Update logic
                            setMachineState(factory.loggingIn(dataState))
                        }
                    }
                }
                else -> super.doProcess(gesture)
            }
            else -> super.doProcess(gesture)
        }
    }

    private fun render(login: LoginDataState) {
        setUiState(renderer.renderLogin(login))
    }
}