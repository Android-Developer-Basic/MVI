package ru.otus.mvi.common.ui

/**
 * Complete set of UI intents
 */
sealed class AppUiIntent {
    /**
     * Content screen gesture
     * @property content Child screen gesture
     */
    data class Content(val content: ContentUiIntent) : AppUiIntent()

    /**
     * Login screen gesture
     * @property login Child screen gesture
     */
    data class Login(val login: LoginUiIntent) : AppUiIntent()
}