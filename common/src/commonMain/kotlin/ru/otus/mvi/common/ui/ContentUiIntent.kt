package ru.otus.mvi.common.ui

/**
 * UI gestures for content state
 */
sealed class ContentUiIntent {
    /**
     * Logout clicked
     */
    data object LogoutClicked : ContentUiIntent()
}