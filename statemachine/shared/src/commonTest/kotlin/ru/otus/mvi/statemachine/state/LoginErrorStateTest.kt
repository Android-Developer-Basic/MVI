package ru.otus.mvi.statemachine.state

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import ru.otus.mvi.common.data.exception.IoException
import ru.otus.mvi.common.data.exception.UnknownException
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.common.ui.LoginUiIntent
import ru.otus.mvi.statemachine.data.AppDataState
import kotlin.test.Test

internal class LoginErrorStateTest : BaseStateTest() {

    private val data = AppDataState()
    private val nonFatalError = IoException("Non-fatal")
    private val fatalError = UnknownException("Fatal")

    override fun doInit() {
        every { renderer.renderLoginError(any(), any()) } returns MOCK_UI_STATE
    }

    @Test
    fun `renders non-fatal error on start`() = test {
        val state = LoginErrorState(context, data, nonFatalError)
        state.start(stateMachine)

        verify(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderLoginError("Non-fatal", true)
            stateMachine.setUiState(MOCK_UI_STATE)
        }
    }

    @Test
    fun `renders fatal error on start`() = test {
        val state = LoginErrorState(context, data, fatalError)
        state.start(stateMachine)

        verify(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderLoginError("Fatal", false)
            stateMachine.setUiState(MOCK_UI_STATE)
        }
    }

    @Test
    fun `retries non-fatal error`() = test {
        val state = LoginErrorState(context, data, nonFatalError)
        every { stateFactory.loggingIn(any()) } returns nextState

        state.start(stateMachine)
        state.process(AppUiIntent.Login(LoginUiIntent.RetryErrorClicked))

        verify(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderLoginError("Non-fatal", true)
            stateMachine.setUiState(MOCK_UI_STATE)
            stateFactory.loggingIn(data)
            stateMachine.setMachineState(nextState)
        }
    }

    @Test
    fun `does not retry fatal error`() = test {
        val state = LoginErrorState(context, data, fatalError)

        state.start(stateMachine)
        state.process(AppUiIntent.Login(LoginUiIntent.RetryErrorClicked))

        verify(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderLoginError("Fatal", false)
            stateMachine.setUiState(MOCK_UI_STATE)
        }
    }

    @Test
    fun `dismisses error`() = test {
        val state = LoginErrorState(context, data, nonFatalError)
        every { stateFactory.loginForm(any()) } returns nextState

        state.start(stateMachine)
        state.process(AppUiIntent.Login(LoginUiIntent.DismissErrorClicked))

        verify(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderLoginError("Non-fatal", true)
            stateMachine.setUiState(MOCK_UI_STATE)
            stateFactory.loginForm(data)
            stateMachine.setMachineState(nextState)
        }
    }
}
