package ru.otus.mvi.statemachine.state

import ru.otus.mvi.common.session.data.User
import ru.otus.mvi.common.ui.AppUiState
import ru.otus.mvi.common.ui.ContentUiState

internal val USER = User(
    username = "user",
    password = "12345",
    email = "user@otus.ru"
)

internal val MOCK_UI_STATE = AppUiState.Content(ContentUiState.Loading)