package ru.otus.mvi.mvi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.common.session.data.Session
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.mvi.data.AppDataState
import ru.otus.mvi.mvi.data.AppIntent
import ru.otus.mvi.mvi.data.ReducerContext
import ru.otus.mvi.mvi.data.reduceContent
import ru.otus.mvi.mvi.data.reduceLogin
import ru.otus.mvi.mvi.data.reduceSession
import ru.otus.mvi.mvi.sessionManager

class AppViewModel(sessionManager: SessionManager, private var data: AppDataState) : ViewModel() {

    /**
     * Reducer assembly
     */
    private val reducer = object : ReducerContext {
        /**
         * Intents source
         */
        private val intents: Channel<AppIntent> = Channel(Channel.BUFFERED)

        override val scope: CoroutineScope get() = this@AppViewModel.viewModelScope
        override val sessionManager: SessionManager = sessionManager
        override fun reduce(intent: AppIntent) {
            intents.trySend(intent)
        }

        val state: Flow<AppDataState> = intents.consumeAsFlow().runningFold(data) { soFar, intent ->
            when(intent) {
                is AppIntent.SessionIntent -> soFar.reduceSession(intent)
                is AppIntent.UiUpdate -> when(intent.ui) {
                    is AppUiIntent.Content -> soFar.reduceContent(intent.ui.content)
                    is AppUiIntent.Login -> soFar.reduceLogin(intent.ui.login)
                }
            }
        }
    }

    /**
     * Watch for session manager session changes
     */
    init {
        viewModelScope.launch {
            sessionManager.session.collectLatest { session ->
                reducer.reduce(
                    when(session) {
                        is Session.Active -> AppIntent.SessionIntent.LoggedIn(session)
                        Session.NotLoggedIn -> AppIntent.SessionIntent.LoggedOut
                    }
                )
            }
        }
    }

    /**
     * Reduced application data state
     */
    val appState: StateFlow<AppDataState> = reducer.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = data
    )

    /**
     * Processes application UI intent
     */
    fun processIntent(intent: AppUiIntent) {
        reducer.reduce(AppIntent.UiUpdate(intent))
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val sessionManager = checkNotNull(this[APPLICATION_KEY]).sessionManager
                AppViewModel(
                    sessionManager,
                    AppDataState()
                )
            }
        }
    }
}