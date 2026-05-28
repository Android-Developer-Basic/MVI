package ru.otus.mvi.statemachine

import com.motorro.commonstatemachine.coroutines.FlowStateMachine
import kotlinx.coroutines.flow.StateFlow
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.common.ui.AppUiState
import ru.otus.mvi.statemachine.data.AppDataState
import ru.otus.mvi.statemachine.state.AppStateFactory
import ru.otus.mvi.statemachine.ui.AppUiRenderer

/**
 * All application logic assembled
 */
class StateMachineAssembly(sessionManager: SessionManager) {
    private val renderer = AppUiRenderer.Impl()
    private val factory = AppStateFactory.Impl(sessionManager, renderer)

    private val stateMachine = FlowStateMachine(renderer.renderContentLoading()) {
        factory.content(AppDataState())
    }

    /**
     * UI state
     */
    val uiState: StateFlow<AppUiState> get() = stateMachine.uiState

    /**
     * Processes intent
     */
    fun processIntent(intent: AppUiIntent) {
        stateMachine.process(intent)
    }

    /**
     * Cleanup
     */
    fun clear() {
        stateMachine.clear()
    }
}