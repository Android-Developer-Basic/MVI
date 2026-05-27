package ru.otus.mvi.composeui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import ru.otus.mvi.common.ui.LoginUiState

@Composable
fun LoginFormScreen(
    uiState: LoginUiState.Form,
    modifier: Modifier = Modifier,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit
) {
    val usernameState = rememberTextFieldState(uiState.username)
    LaunchedEffect(usernameState) {
        snapshotFlow { usernameState.text.toString() }.collectLatest { 
            onUsernameChanged(it)
        }
    }
    val passwordState = rememberTextFieldState(uiState.password)
    LaunchedEffect(passwordState) {
        snapshotFlow { passwordState.text.toString() }.collectLatest {
            onPasswordChanged(it)
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            state = usernameState,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.username)) },
            lineLimits = TextFieldLineLimits.SingleLine
        )
        OutlinedTextField(
            state = passwordState,
            label = { Text(stringResource(Res.string.password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            outputTransformation = OutputTransformation {
                for (i in 0 until length) {
                    replace(i, i + 1, "\u2022")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            lineLimits = TextFieldLineLimits.SingleLine
        )
        Button(
            onClick = onLoginClicked,
            enabled = uiState.loginEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(stringResource(Res.string.login))
        }
    }
}
