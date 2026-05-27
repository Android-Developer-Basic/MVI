package ru.otus.mvi.mvi.ui

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.common.session.data.Session
import ru.otus.mvi.common.session.data.User
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.common.ui.LoginUiIntent
import ru.otus.mvi.mvi.data.AppDataState
import ru.otus.mvi.mvi.data.AppState
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val sessionManager = mockk<SessionManager>()
    private val sessionFlow = MutableStateFlow<Session>(Session.NotLoggedIn)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { sessionManager.session } returns sessionFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is LOGIN because session starts with NotLoggedIn`() = runTest {
        val initialData = AppDataState()
        val viewModel = AppViewModel(sessionManager, initialData)
        
        // Advance dispatcher to allow init block and stateIn to process
        advanceUntilIdle()
        
        assertEquals(AppState.LOGIN, viewModel.appState.value.appState)
    }

    @Test
    fun `processes session updates`() = runTest {
        val viewModel = AppViewModel(sessionManager, AppDataState())
        advanceUntilIdle()
        
        viewModel.appState.test {
            // Should have LOGIN state now
            assertEquals(AppState.LOGIN, awaitItem().appState)
            
            val user = User("test", "pass", "email")
            sessionFlow.value = Session.Active(user)
            
            val stateAfterLogin = awaitItem()
            assertEquals(AppState.CONTENT, stateAfterLogin.appState)
            assertEquals(Session.Active(user), stateAfterLogin.session)
        }
    }

    @Test
    fun `processes UI intents`() = runTest {
        val viewModel = AppViewModel(sessionManager, AppDataState())
        advanceUntilIdle()
        
        viewModel.appState.test {
            assertEquals(AppState.LOGIN, awaitItem().appState)
            
            viewModel.processIntent(AppUiIntent.Login(LoginUiIntent.UsernameChanged("new_user")))
            
            val stateAfterIntent = awaitItem()
            assertEquals("new_user", stateAfterIntent.login.username)
        }
    }
}
