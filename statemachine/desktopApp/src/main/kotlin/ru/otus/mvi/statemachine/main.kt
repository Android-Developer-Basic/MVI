package ru.otus.mvi.statemachine

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.composeui.AppUiScreen

fun main() = application {

    val sessionManager = SessionManager.Instance
    val viewModel = StateMachineAssembly(sessionManager)

    fun cleanup() {
        viewModel.clear()
        exitApplication()
    }

    Window(onCloseRequest = ::cleanup, title = "StateMachine") {
        AppUiScreen(
            uiState = viewModel
                .uiState
                .collectAsState()
                .value,
            onIntent = viewModel::processIntent
        )
    }
}