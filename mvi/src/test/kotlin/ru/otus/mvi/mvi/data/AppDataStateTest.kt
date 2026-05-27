package ru.otus.mvi.mvi.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.otus.mvi.common.data.exception.AppException
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.common.session.data.Session
import ru.otus.mvi.common.session.data.User
import ru.otus.mvi.common.ui.AppUiState
import ru.otus.mvi.common.ui.ContentUiIntent
import ru.otus.mvi.common.ui.ContentUiState
import ru.otus.mvi.common.ui.LoginUiIntent
import ru.otus.mvi.common.ui.LoginUiState
import ru.otus.mvi.common.ui.LogoutUiState
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AppDataStateTest {

    private val sessionManager = mockk<SessionManager>()
    private val reducerContext = mockk<ReducerContext>()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val user = User("testuser", "pass", "test@example.com")
    private val activeSession = Session.Active(user)

    @Before
    fun setup() {
        every { reducerContext.sessionManager } returns sessionManager
        every { reducerContext.scope } returns testScope
        every { reducerContext.reduce(any()) } returns Unit
    }

    @Test
    fun `renders content state when session is active`() {
        val state = AppDataState(appState = AppState.CONTENT, session = activeSession)
        val uiState = state.render()
        
        assertIs<AppUiState.Content>(uiState)
        val content = uiState.content
        assertIs<ContentUiState.Content>(content)
        assertEquals("testuser", content.username)
        assertTrue(content.logoutEnabled)
    }

    @Test
    fun `renders loading when in content state and not logged in`() {
        val state = AppDataState(appState = AppState.CONTENT, session = Session.NotLoggedIn)
        val uiState = state.render()
        
        assertIs<AppUiState.Content>(uiState)
        assertEquals(ContentUiState.Loading, uiState.content)
    }

    @Test
    fun `renders login state`() {
        val loginData = LoginDataState(username = "user")
        val state = AppDataState(appState = AppState.LOGIN, login = loginData)
        val uiState = state.render()
        
        assertIs<AppUiState.Login>(uiState)
        val loginUiState = uiState.login
        assertIs<LoginUiState.Form>(loginUiState)
        assertEquals("user", loginUiState.username)
    }

    @Test
    fun `renders logout state`() {
        val state = AppDataState(appState = AppState.LOGOUT)
        val uiState = state.render()
        
        assertIs<AppUiState.Logout>(uiState)
        assertEquals(LogoutUiState.LoggingOut, uiState.logout)
    }

    @Test
    fun `reduces LoggedIn session intent`() {
        val state = AppDataState(appState = AppState.LOGIN)
        val newState = state.reduceSession(AppIntent.SessionIntent.LoggedIn(activeSession))
        
        assertEquals(AppState.CONTENT, newState.appState)
        assertEquals(activeSession, newState.session)
        assertEquals("", newState.login.password)
        assertEquals(false, newState.login.isRunning)
    }

    @Test
    fun `reduces LoggedOut session intent`() {
        val state = AppDataState(appState = AppState.CONTENT, session = activeSession)
        val newState = state.reduceSession(AppIntent.SessionIntent.LoggedOut)
        
        assertEquals(AppState.LOGIN, newState.appState)
        assertEquals(Session.NotLoggedIn, newState.session)
        assertEquals("", newState.login.password)
        assertEquals(false, newState.login.isRunning)
    }

    @Test
    fun `reduces LoginFailed session intent`() {
        val error = mockk<AppException>()
        val state = AppDataState(appState = AppState.LOGIN, login = LoginDataState(isRunning = true))
        val newState = state.reduceSession(AppIntent.SessionIntent.LoginFailed(error))
        
        assertEquals(AppState.LOGIN, newState.appState)
        assertEquals(Session.NotLoggedIn, newState.session)
        assertEquals(error, newState.login.error)
        assertEquals(false, newState.login.isRunning)
    }

    @Test
    fun `reduces LogoutClicked intent`() = runTest(testDispatcher) {
        val state = AppDataState(appState = AppState.CONTENT, session = activeSession)
        
        coEvery { sessionManager.logout() } returns Unit
        
        with(reducerContext) {
            val newState = state.reduceContent(ContentUiIntent.LogoutClicked)
            assertEquals(AppState.LOGOUT, newState.appState)
        }
        
        coVerify { sessionManager.logout() }
        verify { reducerContext.reduce(AppIntent.SessionIntent.LoggedOut) }
    }

    @Test
    fun `reduces UsernameChanged intent`() {
        val state = AppDataState(appState = AppState.LOGIN)
        with(reducerContext) {
            val newState = state.reduceLogin(LoginUiIntent.UsernameChanged("newuser"))
            assertEquals("newuser", newState.login.username)
        }
    }

    @Test
    fun `reduces LoginClicked intent`() = runTest(testDispatcher) {
        val state = AppDataState(
            appState = AppState.LOGIN, 
            login = LoginDataState(username = "user", password = "password")
        )
        
        coEvery { sessionManager.login("user", "password") } returns activeSession
        
        with(reducerContext) {
            val newState = state.reduceLogin(LoginUiIntent.LoginClicked)
            assertTrue(newState.login.isRunning)
        }
        
        coVerify { sessionManager.login("user", "password") }
        verify { reducerContext.reduce(AppIntent.SessionIntent.LoggedIn(activeSession)) }
    }
}
