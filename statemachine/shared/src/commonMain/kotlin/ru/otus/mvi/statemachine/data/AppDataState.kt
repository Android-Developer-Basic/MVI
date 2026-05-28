package ru.otus.mvi.statemachine.data

/**
 * Complete application state
 */
data class AppDataState(
    val login: LoginDataState = LoginDataState()
)