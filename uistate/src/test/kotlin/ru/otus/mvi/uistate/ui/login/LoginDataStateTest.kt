package ru.otus.mvi.uistate.ui.login

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.otus.mvi.common.data.exception.IoException
import ru.otus.mvi.common.data.exception.UnknownException
import ru.otus.mvi.common.ui.LoginUiState

class LoginDataStateTest {

    @Test
    fun `hasValidForm returns true only when both fields are not blank`() {
        assertFalse(LoginDataState("", "").hasValidForm())
        assertFalse(LoginDataState(" ", " ").hasValidForm())
        assertFalse(LoginDataState("user", "").hasValidForm())
        assertFalse(LoginDataState("", "password").hasValidForm())
        assertTrue(LoginDataState("user", "password").hasValidForm())
    }

    @Test
    fun `render returns LoggingIn when isRunning is true`() {
        val state = LoginDataState("user", "password", isRunning = true)
        val uiState = state.render()
        
        assertTrue(uiState is LoginUiState.LoggingIn)
        assertEquals("user", (uiState as LoginUiState.LoggingIn).username)
        assertEquals("password", uiState.password)
    }

    @Test
    fun `render returns Error when error is present and not running`() {
        val error = IoException("Network error") // Non-fatal
        val state = LoginDataState("user", "password", isRunning = false, error = error)
        val uiState = state.render()

        assertTrue(uiState is LoginUiState.Error)
        assertEquals("Network error", (uiState as LoginUiState.Error).message)
        assertTrue(uiState.retryAvailable)
    }

    @Test
    fun `render returns Error with no retry when fatal error is present`() {
        val error = UnknownException("Fatal error") // Fatal
        val state = LoginDataState("user", "password", isRunning = false, error = error)
        val uiState = state.render()

        assertTrue(uiState is LoginUiState.Error)
        assertEquals("Fatal error", (uiState as LoginUiState.Error).message)
        assertFalse(uiState.retryAvailable)
    }

    @Test
    fun `render returns Form when not running and no error`() {
        val state = LoginDataState("user", "password", isRunning = false, error = null)
        val uiState = state.render()

        assertTrue(uiState is LoginUiState.Form)
        assertEquals("user", (uiState as LoginUiState.Form).username)
        assertEquals("password", uiState.password)
        assertTrue(uiState.loginEnabled)
    }

    @Test
    fun `render returns Form with login disabled when fields are empty`() {
        val state = LoginDataState("", "", isRunning = false, error = null)
        val uiState = state.render()

        assertTrue(uiState is LoginUiState.Form)
        assertFalse((uiState as LoginUiState.Form).loginEnabled)
    }
}
