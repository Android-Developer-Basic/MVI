package ru.otus.mvi.mvvm.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.otus.mvi.common.data.exception.AppException
import ru.otus.mvi.common.data.exception.toAppException
import ru.otus.mvi.mvvm.sessionManager

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * Error if any
     */
    val error: StateFlow<AppException?> field = MutableStateFlow(null)
    /**
     * Logging-in
     */
    val loggingIn: StateFlow<Boolean> field = MutableStateFlow(false)

    /**
     * Navigation events to be observed by the UI.
     */
    val navigationEvents: ReceiveChannel<LoginNavigationEvent> field = Channel<LoginNavigationEvent>(Channel.BUFFERED)

    /**
     * Attempts login
     * @param username User name
     * @param password Password
     */
    fun login(username: String, password: String) = viewModelScope.launch {
        Napier.d { "Logging in user: $username" }

        loggingIn.value = true
        error.value = null

        try {
            application.sessionManager.login(username, password)
            Napier.d { "Successfully logged in. Navigating to content..." }
            navigationEvents.trySend(LoginNavigationEvent.NavigateToContent)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()
            Napier.w(e) { "Login error:" }
            error.emit(e.toAppException())
        } finally {
            loggingIn.value = false
        }
    }

    /**
     * Clears error
     */
    fun clearError() {
        error.value = null
    }
}
