package ru.otus.mvi.composeui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.otus.mvi.common.ui.LoginUiState

@Composable
fun LoginErrorScreen(
    uiState: LoginUiState.Error,
    modifier: Modifier = Modifier,
    onErrorDismissed: () -> Unit,
    onErrorRetried: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = uiState.message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )

        if (uiState.retryAvailable) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onErrorRetried) {
                Text(stringResource(Res.string.retry))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onErrorDismissed) {
            Text(stringResource(Res.string.cancel))
        }
    }
}
