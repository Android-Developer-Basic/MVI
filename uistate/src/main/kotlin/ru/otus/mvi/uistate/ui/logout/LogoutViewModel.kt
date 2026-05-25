package ru.otus.mvi.uistate.ui.logout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.common.ui.LogoutUiState

class LogoutViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * UI state
     */
    val uiState: StateFlow<LogoutUiState> field = MutableStateFlow<LogoutUiState>(LogoutUiState.LoggingOut)

    /**
     * Navigation events to be observed by the UI.
     */
    val navigationEvents: ReceiveChannel<LogoutNavigationEvent> field = Channel<LogoutNavigationEvent>(Channel.BUFFERED)

    init {
        Napier.d { "Logging out..." }
        viewModelScope.launch {
            SessionManager.Instance.logout()
            navigationEvents.trySend(LogoutNavigationEvent.NavigateToContent)
        }
    }
}