package ru.otus.mvi.uistate.ui.content

/**
 * Defines possible navigation events that can be triggered from this ViewModel.
 */
sealed interface ContentNavigationEvent {
    /**
     * Represents a navigation request to the login screen/fragment.
     */
    object NavigateToLogin : ContentNavigationEvent

    /**
     * Represents a navigation request to the logout screen/fragment.
     */
    object NavigateToLogout : ContentNavigationEvent
}