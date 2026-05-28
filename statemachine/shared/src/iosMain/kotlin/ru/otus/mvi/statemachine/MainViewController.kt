package ru.otus.mvi.statemachine

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.composeui.AppUiScreen

fun MainViewController() = ComposeUIViewController {
    val viewModel = remember {
        val sessionManager = SessionManager.Instance
        StateMachineAssembly(sessionManager)
    }
    AppUiScreen(
        uiState = viewModel
            .uiState
            .collectAsStateWithLifecycle()
            .value,
        onIntent = viewModel::processIntent
    )
}