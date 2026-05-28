package ru.otus.mvi.statemachine.state

import com.motorro.commonstatemachine.CommonMachineState
import com.motorro.commonstatemachine.coroutines.CoroutineState
import io.github.aakira.napier.Napier
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.common.ui.AppUiState

/**
 * Application state type
 */
typealias AppState = CommonMachineState<AppUiIntent, AppUiState>

/**
 * Base application state
 */
internal abstract class BaseAppState(context: AppContext) : CoroutineState<AppUiIntent, AppUiState>(), AppContext by context {
    override fun doStart() {
        Napier.d { "Starting ${this::class.simpleName}" }
    }

    override fun doProcess(gesture: AppUiIntent) {
        Napier.w { "Unknown gesture: $gesture" }
    }

    override fun doClear() {
        Napier.d { "Stopping ${this::class.simpleName}" }
        super.doClear()
    }
}