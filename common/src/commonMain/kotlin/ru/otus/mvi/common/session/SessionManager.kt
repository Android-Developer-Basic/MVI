package ru.otus.mvi.common.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import ru.otus.mvi.common.data.exception.ConflictException
import ru.otus.mvi.common.data.exception.IoException
import ru.otus.mvi.common.data.exception.UnauthorizedException
import ru.otus.mvi.common.session.data.Session
import ru.otus.mvi.common.session.data.User
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Session manager
 */
interface SessionManager {
    /**
     * Current session state
     */
    val session: StateFlow<Session>

    /**
     * Login user
     * @param username username
     * @param password password
     * @return result of login
     */
    suspend fun login(username: String, password: String): Session.Active

    /**
     * Register user
     * @param user user to register
     * @return result of registration
     */
    suspend fun register(user: User): Session.Active

    /**
     * Logout user
     */
    suspend fun logout()

    companion object {
        private val user = User(
            "user",
            "password",
            "user@otus.ru"
        )

        /**
         * Session manager with delay
         */
        @OptIn(DelicateCoroutinesApi::class)
        val Instance: SessionManager = MockSessionManager(
            scope = GlobalScope,
            delay = 1000.milliseconds,
            users = setOf(user)
        ) { (1..10).random() <= 4 }
    }
}

/**
 * Session manager mock implementation
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class MockSessionManager(scope: CoroutineScope, private val delay: Duration, users: Set<User> = emptySet(), private val willFail: () -> Boolean) : SessionManager {

    private suspend inline fun <T> runOperation(block: () -> T): T {
        delay(delay)
        return if (willFail()) {
            throw IoException("Network error")
        } else {
            block()
        }
    }

    private class SessionState(val users: Map<String, User>, val selected: String?) {
        fun login(username: String, password: String): SessionState {
            val key = username.lowercase()
            if (null == users[key]?.takeIf { password == it.password }) {
                throw UnauthorizedException("Invalid username or password")
            }
            return SessionState(users, key)
        }

        fun logout(): SessionState = SessionState(users, null)

        fun register(user: User): SessionState {
            val key = user.username.lowercase()
            if (null != users[key]) {
                throw ConflictException("User already exists")
            }
            return SessionState(users + (key to user), key)
        }

        fun getSession(): Session = when (selected) {
            null -> Session.NotLoggedIn
            else -> Session.Active(users.getValue(selected))
        }
    }

    private val users = MutableStateFlow(SessionState(users.associateBy { it.username.lowercase() }, null))

    override val session: StateFlow<Session> = this.users.map { it.getSession() }.stateIn(
        scope,
        SharingStarted.Eagerly,
        Session.NotLoggedIn
    )

    override suspend fun login(username: String, password: String): Session.Active = runOperation {
        val state = users.updateAndGet { state ->
            state.login(username, password)
        }
        state.getSession() as Session.Active
    }

    override suspend fun register(user: User): Session.Active = runOperation {
        val state = users.updateAndGet { state ->
            state.register(user)
        }
        state.getSession() as Session.Active
    }

    override suspend fun logout() {
        delay(delay)
        users.update { it.logout() }
    }
}

