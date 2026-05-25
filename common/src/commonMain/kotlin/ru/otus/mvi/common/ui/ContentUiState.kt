package ru.otus.mvi.common.ui

/**
 * Content UI state
 */
sealed class ContentUiState {
    /**
     * Loading data
     */
    data object Loading : ContentUiState()

    /**
     * Content
     * @property username Active user
     * @property logoutEnabled Is logout button enabled
     */
    data class Content(val username: String, val logoutEnabled: Boolean) : ContentUiState()
}

