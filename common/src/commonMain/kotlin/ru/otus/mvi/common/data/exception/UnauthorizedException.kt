package ru.otus.mvi.common.data.exception

/**
 * Unauthorized exception
 * Always fatal as we couldn't retry with the same data
 */
data class UnauthorizedException(override val message: String) : AppException() {
    override val isFatal: Boolean get() = true
}