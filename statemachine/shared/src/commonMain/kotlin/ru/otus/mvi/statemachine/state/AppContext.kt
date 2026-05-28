package ru.otus.mvi.statemachine.state

import ru.otus.mvi.statemachine.ui.AppUiRenderer

/**
 * Common context for all states
 */
internal interface AppContext {
    /**
     * State factory
     */
    val factory: AppStateFactory

    /**
     * UI renderer
     */
    val renderer: AppUiRenderer
}