package ru.otus.mvi.statemachine.state

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import ru.otus.mvi.statemachine.data.AppDataState
import ru.otus.mvi.statemachine.data.LoginDataState
import kotlin.test.Test

internal class LoggingOutStateTest : BaseStateTest() {

    private val loginData = LoginDataState(USER.username, USER.password)
    private val data = AppDataState(login = loginData)
    private lateinit var state: LoggingOutState

    override fun doInit() {
        every { renderer.renderLoggingOut() } returns MOCK_UI_STATE
        state = LoggingOutState(context, data, sessionManager)
    }

    @Test
    fun `logs out and transfers to content`() = test {
        everySuspend { sessionManager.logout() } returns Unit
        val expectedData = data.copy(login = loginData.copy(password = ""))
        every { stateFactory.content(expectedData) } returns nextState

        state.start(stateMachine)

        verifySuspend(mode = VerifyMode.exhaustiveOrder) {
            renderer.renderLoggingOut()
            stateMachine.setUiState(MOCK_UI_STATE)
            sessionManager.logout()
            stateFactory.content(expectedData)
            stateMachine.setMachineState(nextState)
        }
    }
}
