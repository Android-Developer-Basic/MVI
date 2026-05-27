package ru.otus.mvi.composeui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.otus.mvi.common.ui.LoginUiState

@Composable
fun LoggingInScreen(
    uiState: LoginUiState.LoggingIn,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LoginFormScreen(
            uiState = LoginUiState.Form(
                username = uiState.username,
                password = uiState.password,
                loginEnabled = false
            ),
            onUsernameChanged = {},
            onPasswordChanged = {},
            onLoginClicked = {}
        )
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )
    }
}
