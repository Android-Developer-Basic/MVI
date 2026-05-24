package ru.otus.mvi.mvvm.ui.logout

sealed interface LogoutNavigationEvent {
    /**
     * Represents a navigation request to the content screen.
     */
    data object NavigateToContent : LogoutNavigationEvent
}