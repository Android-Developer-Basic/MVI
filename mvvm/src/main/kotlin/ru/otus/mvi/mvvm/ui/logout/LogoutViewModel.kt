package ru.otus.mvi.mvvm.ui.logout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import ru.otus.mvi.common.session.SessionManager

class LogoutViewModel(application: Application) : AndroidViewModel(application) {
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