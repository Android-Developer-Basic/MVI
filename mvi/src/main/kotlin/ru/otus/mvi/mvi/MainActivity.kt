package ru.otus.mvi.mvi

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.otus.mvi.composeui.AppUiScreen
import ru.otus.mvi.mvi.data.render
import ru.otus.mvi.mvi.ui.AppViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: AppViewModel by viewModels {
        AppViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppUiScreen(
                uiState = viewModel
                    .appState
                    .collectAsStateWithLifecycle()
                    .value
                    .render(),
                onIntent = viewModel::processIntent
            )
        }
    }
}
