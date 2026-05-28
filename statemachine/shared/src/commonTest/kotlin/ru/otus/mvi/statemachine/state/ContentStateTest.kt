package ru.otus.mvi.statemachine.state

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import ru.otus.mvi.common.session.data.Session
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.common.ui.ContentUiIntent
import ru.otus.mvi.statemachine.data.AppDataState
import kotlin.test.Test

internal class ContentStateTest : BaseStateTest() {

    private val data = AppDataState()

    private lateinit var state: BaseAppState

    override fun doInit() {
        every { renderer.renderContentLoading() } returns MOCK_UI_STATE
        every { renderer.renderContent(any()) } returns MOCK_UI_STATE

        state = ContentState(context, data, sessionManager)
    }

    @Test
    fun `renders loading and content if logged in`() = test {
        every { sessionManager.session } returns flowOf(Session.Active(USER)).stateIn(backgroundScope)
        state.start(stateMachine)

        verify(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderContentLoading()
            stateMachine.setUiState(MOCK_UI_STATE)
            sessionManager.session
            renderer.renderContent(USER.username)
            stateMachine.setUiState(MOCK_UI_STATE)
        }
    }

    @Test
    fun `switches to login if no active session`() = test {
        every { sessionManager.session } returns flowOf(Session.NotLoggedIn).stateIn(backgroundScope)
        every { stateFactory.loginForm(any()) } returns nextState
        state.start(stateMachine)

        verify(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderContentLoading()
            stateMachine.setUiState(MOCK_UI_STATE)
            sessionManager.session
            stateFactory.loginForm(data)
            stateMachine.setMachineState(nextState)
        }
    }

    @Test
    fun `switches to logout on logout gesture`() = test {
        every { sessionManager.session } returns flowOf(Session.Active(USER)).stateIn(backgroundScope)
        every { stateFactory.loggingOut(any()) } returns nextState
        state.start(stateMachine)
        state.process(AppUiIntent.Content(ContentUiIntent.LogoutClicked))

        verify {
            stateFactory.loggingOut(data)
            stateMachine.setMachineState(nextState)
        }
    }
}