package ru.otus.mvi.mvvm.ui.content

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.otus.mvi.common.session.data.Session
import ru.otus.mvi.mvvm.sessionManager

class ContentFragmentViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * Loading status
     */
    val isLoading: StateFlow<Boolean> field = MutableStateFlow(false)

    /**
     * User-name
     */
    val userName: StateFlow<String> field = MutableStateFlow("user")

    init {
        // Listens to the Session Manager.
        // If we have active session - shows content.
        // If we don't - navigates to log in.
        viewModelScope.launch {
            Napier.d { "Subscribing session..." }
            isLoading.emit(true)
            application.sessionManager.session.collect { session ->
                isLoading.emit(false)
                when (session) {
                    is Session.Active -> {
                        Napier.d { "Has active user: ${session.user.username}" }
                        userName.emit(session.user.username)
                    }
                    Session.NotLoggedIn -> {
                        Napier.d { "No active user. Navigating to login..." }
                        navigateToLogin()
                    }
                }
            }
        }
    }
    
    /**
     * Navigation events to be observed by the UI.
     *
     * WHY CHANNEL IS BETTER THAN SHARED-FLOW FOR EVENTS:
     * 1. GUARANTEED DELIVERY: Channel(BUFFERED) stores events even if there are no active
     *    collectors. When the UI (Fragment) becomes active again, it will receive the
     *    buffered events.
     * 2. ONE-TIME CONSUMPTION: Once an event is received, it is removed from the channel.
     *    This prevents the "re-delivery" problem during configuration changes (unlike
     *    SharedFlow with replay=1).
     * 3. SINGLE OBSERVER FOCUS: Perfect for navigation where you only want one component
     *    to handle the event once.
     */
    val navigationEvents: ReceiveChannel<ContentNavigationEvent> field = Channel<ContentNavigationEvent>(Channel.BUFFERED)

    /**
     * Triggers a navigation event to the login fragment/screen.
     */
    private fun navigateToLogin() = viewModelScope.launch {
        navigationEvents.send(ContentNavigationEvent.NavigateToLogin)
    }

    /**
     * 
     */
    fun logout() = viewModelScope.launch {
        Napier.d { "Navigating to logout..." }
        navigationEvents.send(ContentNavigationEvent.NavigateToLogout)
    }
}
