package ru.otus.mvi.uistate.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.otus.mvi.common.data.exception.toAppException
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.common.ui.LoginUiState
import ru.otus.mvi.uistate.sessionManager

class LoginViewModel(private val sessionManager: SessionManager, private var dataState: LoginDataState) : ViewModel() {
    /**
     * UI state
     */
    val uiState: StateFlow<LoginUiState> field = MutableStateFlow<LoginUiState>(dataState.render())

    /**
     * Navigation events to be observed by the UI.
     */
    val navigationEvents: ReceiveChannel<LoginNavigationEvent> field = Channel<LoginNavigationEvent>(Channel.BUFFERED)

    /**
     * Updates current data state
     */
    private inline fun reduceData(block: LoginDataState.() -> LoginDataState) {
        val newState = dataState.block()
        dataState = newState
        uiState.value = newState.render()
    }

    /**
     * Updates current username
     */
    fun setUsername(username: String) {
        reduceData {
            copy(username = username)
        }
    }

    /**
     * Updates current password
     */
    fun setPassword(password: String) {
        reduceData {
            copy(password = password)
        }
    }

    /**
     * Attempts login
     */
    fun login() {
        val data = dataState

        if (data.hasValidForm().not()) {
            Napier.w { "Invalid form. Can't login" }
            return
        }

        viewModelScope.launch {
            Napier.d { "Logging in user: ${data.username}" }
            reduceData {
                copy(
                    isRunning = true,
                    error = null
                )
            }

            try {
                sessionManager.login(data.username, data.password)
                Napier.d { "Successfully logged in. Navigating to content..." }
                navigationEvents.trySend(LoginNavigationEvent.NavigateToContent)
            } catch (e: Throwable) {
                currentCoroutineContext().ensureActive()
                Napier.w(e) { "Login error:" }
                reduceData {
                    copy(
                        isRunning = false,
                        error = e.toAppException()
                    )
                }
            }
        }
    }

    /**
     * Clears error
     */
    fun clearError() {
        reduceData {
            copy(error = null)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val sessionManager = checkNotNull(this[APPLICATION_KEY]).sessionManager
                LoginViewModel(
                    sessionManager,
                    LoginDataState(username = "", password = "")
                )
            }
        }
    }
}
