package ru.otus.mvi.mvvm.ui.login

sealed interface LoginNavigationEvent {
    /**
     * Represents a navigation request to the content screen.
     */
    data object NavigateToContent : LoginNavigationEvent
}