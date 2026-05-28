package ru.otus.mvi.statemachine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.otus.mvi.composeui.AppUiScreen

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels {
        AppViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            AppUiScreen(
                uiState = viewModel
                    .uiState
                    .collectAsStateWithLifecycle()
                    .value,
                onIntent = viewModel::processIntent
            )
        }
    }
}