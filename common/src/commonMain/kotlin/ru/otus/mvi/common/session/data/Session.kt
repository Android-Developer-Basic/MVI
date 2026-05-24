package ru.otus.mvi.common.session.data

/**
 * Session
 */
sealed class Session {
    /**
     * Not logged in
     */
    data object NotLoggedIn : Session()

    /**
     * Logged in
     * @property user The user data
     */
    data class Active(val user: User) : Session()
}