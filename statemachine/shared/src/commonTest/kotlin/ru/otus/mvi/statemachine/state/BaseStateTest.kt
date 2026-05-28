package ru.otus.mvi.statemachine.state

import com.motorro.commonstatemachine.CommonMachineState
import com.motorro.commonstatemachine.CommonStateMachine
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import ru.otus.mvi.common.session.SessionManager
import ru.otus.mvi.common.ui.AppUiIntent
import ru.otus.mvi.common.ui.AppUiState
import ru.otus.mvi.statemachine.ui.AppUiRenderer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
internal abstract class BaseStateTest {

    protected lateinit var sessionManager: SessionManager
    protected lateinit var stateFactory: AppStateFactory
    protected lateinit var renderer: AppUiRenderer
    protected lateinit var context: AppContext
    protected lateinit var stateMachine: CommonStateMachine<AppUiIntent, AppUiState>

    protected lateinit var nextState: CommonMachineState<AppUiIntent, AppUiState>
    protected lateinit var dispatcher: TestDispatcher

    @BeforeTest
    fun init() {
        sessionManager = mock()
        stateFactory = mock()
        renderer = mock()

        context = object : AppContext {
            override val factory: AppStateFactory get() = this@BaseStateTest.stateFactory
            override val renderer: AppUiRenderer get() = this@BaseStateTest.renderer
        }

        stateMachine = mock {
            every { this@mock.setUiState(any()) } returns Unit
            every { this@mock.setMachineState(any()) } returns Unit
        }

        nextState = object : CommonMachineState<AppUiIntent, AppUiState>() { }
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)

        doInit()
    }

    protected open fun doInit() = Unit

    @AfterTest
    fun deinit() {
        Dispatchers.resetMain()
    }

    protected fun test(testBody: suspend TestScope.() -> Unit) = runTest(dispatcher) {
        testBody()
    }
}