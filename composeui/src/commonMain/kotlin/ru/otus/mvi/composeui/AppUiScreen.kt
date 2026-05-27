package ru.otus.mvi.composeui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.common.ui.AppUiState
import ru.otus.mvi.common.ui.ContentUiIntent
import ru.otus.mvi.common.ui.LoginUiIntent
import ru.otus.mvi.common.ui.LoginUiState

@Composable
fun AppUiScreen(
    uiState: AppUiState,
    modifier: Modifier = Modifier,
    onIntent: (AppUiIntent) -> Unit
) {
    // Get title
    val title = when(uiState) {
        is AppUiState.Content -> stringResource(Res.string.content_title)
        is AppUiState.Login -> stringResource(Res.string.login)
        is AppUiState.Logout -> stringResource(Res.string.logout)
    }

    // Remember UI handlers
    val onLogout = remember {
        { onIntent(AppUiIntent.Content(ContentUiIntent.LogoutClicked)) }
    }
    val onUsernameChanged = remember {
        { value: String -> onIntent(AppUiIntent.Login(LoginUiIntent.UsernameChanged(value)))}
    }
    val onPasswordChanged = remember {
        { value: String -> onIntent(AppUiIntent.Login(LoginUiIntent.PasswordChanged(value)))}
    }
    val onLoginClicked = remember {
        { onIntent(AppUiIntent.Login(LoginUiIntent.LoginClicked))}
    }
    val onDismissClicked = remember {
        { onIntent(AppUiIntent.Login(LoginUiIntent.DismissErrorClicked))}
    }
    val onRetryClicked = remember {
        { onIntent(AppUiIntent.Login(LoginUiIntent.RetryErrorClicked))}
    }

    // Build screen
    CommonScaffold(title = title, modifier = modifier) { paddingValues ->
        val modifier = remember {
            Modifier.padding(paddingValues)
        }
        when(uiState) {
            is AppUiState.Content -> ContentScreen(
                uiState = uiState.content,
                modifier = modifier,
                onLogout = onLogout
            )
            is AppUiState.Login -> when(val loginState = uiState.login) {
                is LoginUiState.Form -> LoginFormScreen(
                    uiState = loginState,
                    modifier = modifier,
                    onUsernameChanged = onUsernameChanged,
                    onPasswordChanged = onPasswordChanged,
                    onLoginClicked = onLoginClicked
                )
                is LoginUiState.LoggingIn -> LoggingInScreen(
                    uiState = loginState,
                    modifier = modifier
                )
                is LoginUiState.Error -> LoginErrorScreen(
                    uiState = loginState,
                    modifier = modifier,
                    onErrorDismissed = onDismissClicked,
                    onErrorRetried = onRetryClicked
                )
            }
            is AppUiState.Logout -> LoggingOutScreen(
                modifier = modifier
            )
        }
    }
}