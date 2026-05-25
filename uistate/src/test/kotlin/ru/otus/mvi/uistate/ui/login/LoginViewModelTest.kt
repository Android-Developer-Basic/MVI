package ru.otus.mvi.uistate.ui.login

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.otus.mvi.common.data.exception.UnauthorizedException
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.common.session.data.Session
import ru.otus.mvi.common.session.data.User
import ru.otus.mvi.common.ui.LoginUiState

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val sessionManager = mockk<SessionManager>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setUsername updates UI state`() = runTest {
        val viewModel = LoginViewModel(sessionManager, LoginDataState("", ""))

        viewModel.uiState.test {
            assertEquals(LoginUiState.Form("", "", false), awaitItem())
            
            viewModel.setUsername("user")
            assertEquals(LoginUiState.Form("user", "", false), awaitItem())
        }
    }

    @Test
    fun `setPassword updates UI state`() = runTest {
        val viewModel = LoginViewModel(sessionManager, LoginDataState("", ""))

        viewModel.uiState.test {
            assertEquals(LoginUiState.Form("", "", false), awaitItem())
            
            viewModel.setPassword("password")
            assertEquals(LoginUiState.Form("", "password", false), awaitItem())
        }
    }

    @Test
    fun `login button enabled when both fields filled`() = runTest {
        val viewModel = LoginViewModel(sessionManager, LoginDataState("", ""))

        viewModel.uiState.test {
            awaitItem() // Initial
            viewModel.setUsername("user")
            awaitItem()
            viewModel.setPassword("password")
            assertEquals(LoginUiState.Form("user", "password", true), awaitItem())
        }
    }

    @Test
    fun `login success navigates to content`() = runTest {
        val viewModel = LoginViewModel(sessionManager, LoginDataState("user", "password"))
        val activeSession = Session.Active(User("user", "password", "email"))
        coEvery { sessionManager.login("user", "password") } returns activeSession

        viewModel.login()

        val event = viewModel.navigationEvents.receive()
        assertEquals(LoginNavigationEvent.NavigateToContent, event)
        coVerify { sessionManager.login("user", "password") }
    }

    @Test
    fun `login failure shows error`() = runTest {
        val viewModel = LoginViewModel(sessionManager, LoginDataState("user", "password"))
        val exception = UnauthorizedException("Login failed")
        
        coEvery { sessionManager.login("user", "password") } coAnswers {
            delay(10)
            throw exception
        }

        viewModel.uiState.test {
            assertEquals(LoginUiState.Form("user", "password", true), awaitItem())
            
            viewModel.login()
            
            // Should show LoggingIn first
            val loggingIn = awaitItem()
            assertTrue("Expected LoggingIn but got $loggingIn", loggingIn is LoginUiState.LoggingIn)
            
            // Then Error
            val errorState = awaitItem()
            assertTrue("Expected Error but got $errorState", errorState is LoginUiState.Error)
            assertEquals("Login failed", (errorState as LoginUiState.Error).message)
        }
    }
}
