package ru.otus.mvi.statemachine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.StateFlow
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.common.ui.AppUiState

/**
 * Hosts state machine
 */
class AppViewModel(private val assembly: StateMachineAssembly) : ViewModel() {

    val uiState: StateFlow<AppUiState> get() = assembly.uiState

    fun processIntent(intent: AppUiIntent) {
        assembly.processIntent(intent)
    }

    override fun onCleared() {
        assembly.clear()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val sessionManager = checkNotNull(this[APPLICATION_KEY]).sessionManager
                AppViewModel(
                    StateMachineAssembly(sessionManager)
                )
            }
        }
    }
}