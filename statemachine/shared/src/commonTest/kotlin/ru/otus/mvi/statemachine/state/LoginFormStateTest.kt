package ru.otus.mvi.statemachine.state

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.common.ui.LoginUiIntent
import ru.otus.mvi.statemachine.data.AppDataState
import ru.otus.mvi.statemachine.data.LoginDataState
import kotlin.test.Test

internal class LoginFormStateTest : BaseStateTest() {

    private val data = AppDataState()
    private lateinit var state: LoginFormState

    override fun doInit() {
        every { renderer.renderLogin(any()) } returns MOCK_UI_STATE
        state = LoginFormState(context, data)
    }

    @Test
    fun `renders login form on start`() = test {
        state.start(stateMachine)

        verify(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderLogin(data.login)
            stateMachine.setUiState(MOCK_UI_STATE)
        }
    }

    @Test
    fun `updates username`() = test {
        state.start(stateMachine)
        state.process(AppUiIntent.Login(LoginUiIntent.UsernameChanged("new_user")))

        val expected = data.login.copy(username = "new_user")
        verify(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderLogin(data.login)
            stateMachine.setUiState(MOCK_UI_STATE)
            renderer.renderLogin(expected)
            stateMachine.setUiState(MOCK_UI_STATE)
        }
    }

    @Test
    fun `updates password`() = test {
        state.start(stateMachine)
        state.process(AppUiIntent.Login(LoginUiIntent.PasswordChanged("new_password")))

        val expected = data.login.copy(password = "new_password")
        verify(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderLogin(data.login)
            stateMachine.setUiState(MOCK_UI_STATE)
            renderer.renderLogin(expected)
            stateMachine.setUiState(MOCK_UI_STATE)
        }
    }

    @Test
    fun `transfers to logging-in if form is valid`() = test {
        val validData = AppDataState(LoginDataState("user", "pass"))
        state = LoginFormState(context, validData)
        every { stateFactory.loggingIn(any()) } returns nextState

        state.start(stateMachine)
        state.process(AppUiIntent.Login(LoginUiIntent.LoginClicked))

        verify(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderLogin(validData.login)
            stateMachine.setUiState(MOCK_UI_STATE)
            stateFactory.loggingIn(validData)
            stateMachine.setMachineState(nextState)
        }
    }

    @Test
    fun `does not transfer if form is invalid`() = test {
        state.start(stateMachine)
        state.process(AppUiIntent.Login(LoginUiIntent.LoginClicked))

        verify(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderLogin(data.login)
            stateMachine.setUiState(MOCK_UI_STATE)
        }
    }
}
