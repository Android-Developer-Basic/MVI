package ru.otus.mvi.common.session

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import ru.otus.mvi.common.data.exception.ConflictException
import ru.otus.mvi.common.data.exception.UnauthorizedException
import ru.otus.mvi.common.session.data.Session
import ru.otus.mvi.common.session.data.User
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class MockSessionManagerTest {
    private lateinit var sessionManager: SessionManager
    private lateinit var dispatcher: TestDispatcher

    @BeforeTest
    fun init() {
        dispatcher = UnconfinedTestDispatcher()
    }

    private fun TestScope.createManager(users: Set<User> = emptySet()) {
        sessionManager = MockSessionManager(backgroundScope, 0.seconds, users) { false }
    }

    private inline fun test(crossinline block: suspend TestScope.() -> Unit) = runTest(dispatcher) {
        block()
    }

    @Test
    fun startsWithNoSession() = test {
        createManager()
        assertIs<Session.NotLoggedIn>(sessionManager.session.value)
    }

    @Test
    fun successfullyLogsIn() = test {
        createManager(setOf(user1, user2))
        val changes = backgroundScope.async {
            sessionManager.session.take(2).toList()
        }

        val session = sessionManager.login("IVAN", "12345")
        assertEquals(user1, session.user)

        changes.await().let {
            assertIs<Session.NotLoggedIn>(it.first())
            assertIs<Session.Active>(it.last())
        }
    }

    @Test
    fun failsToLogin() = test {
        createManager(setOf(user1, user2))

        assertFailsWith<UnauthorizedException> {
            sessionManager.login("John", "00000")
        }

        assertIs<Session.NotLoggedIn>(sessionManager.session.value)
    }

    @Test
    fun registers() = test {
        createManager()
        val changes = backgroundScope.async {
            sessionManager.session.take(2).toList()
        }

        val session = sessionManager.register(user1)
        assertEquals(user1, session.user)

        changes.await().let {
            assertIs<Session.NotLoggedIn>(it.first())
            assertIs<Session.Active>(it.last())
        }
    }

    @Test
    fun failsToRegisterIfUserExists() = test {
        createManager(setOf(user1, user2))

        assertFailsWith<ConflictException> {
            sessionManager.register(user1)
        }

        assertIs<Session.NotLoggedIn>(sessionManager.session.value)
    }

    @Test
    fun logsOut() = test {
        createManager(setOf(user1, user2))
        val changes = backgroundScope.async {
            sessionManager.session.take(3).toList()
        }

        sessionManager.login("IVAN", "12345")
        sessionManager.logout()

        changes.await().let {
            assertIs<Session.NotLoggedIn>(it[0])
            assertIs<Session.Active>(it[1])
            assertIs<Session.NotLoggedIn>(it[2])
        }
    }
}