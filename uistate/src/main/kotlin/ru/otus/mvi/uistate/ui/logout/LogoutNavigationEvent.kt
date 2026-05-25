package ru.otus.mvi.uistate.ui.logout

sealed interface LogoutNavigationEvent {
    /**
     * Represents a navigation request to the content screen.
     */
    data object NavigateToContent : LogoutNavigationEvent
}