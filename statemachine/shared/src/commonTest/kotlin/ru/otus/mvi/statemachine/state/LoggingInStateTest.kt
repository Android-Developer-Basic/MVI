package ru.otus.mvi.statemachine.state

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import ru.otus.mvi.common.data.exception.IoException
import ru.otus.mvi.common.session.data.Session
import ru.otus.mvi.statemachine.data.AppDataState
import ru.otus.mvi.statemachine.data.LoginDataState
import kotlin.test.Test

internal class LoggingInStateTest : BaseStateTest() {

    private val loginData = LoginDataState(USER.username, USER.password)
    private val data = AppDataState(login = loginData)
    private lateinit var state: LoggingInState

    override fun doInit() {
        every { renderer.renderLoggingIn(any()) } returns MOCK_UI_STATE
        state = LoggingInState(context, data, sessionManager)
    }

    @Test
    fun `logs in and transfers to content`() = test {
        val session = Session.Active(USER)
        everySuspend { sessionManager.login(USER.username, USER.password) } returns session
        every { stateFactory.content(data) } returns nextState

        state.start(stateMachine)

        verifySuspend(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderLoggingIn(loginData)
            stateMachine.setUiState(MOCK_UI_STATE)
            sessionManager.login(USER.username, USER.password)
            stateFactory.content(data)
            stateMachine.setMachineState(nextState)
        }
    }

    @Test
    fun `transfers to error if login fails`() = test {
        val error = IoException("Login failed")
        everySuspend { sessionManager.login(USER.username, USER.password) } throws error
        every { stateFactory.loginError(data, error) } returns nextState

        state.start(stateMachine)

        verifySuspend(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderLoggingIn(loginData)
            stateMachine.setUiState(MOCK_UI_STATE)
            sessionManager.login(USER.username, USER.password)
            stateFactory.loginError(data, error)
            stateMachine.setMachineState(nextState)
        }
    }
}
