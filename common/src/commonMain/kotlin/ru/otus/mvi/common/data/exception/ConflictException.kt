package ru.otus.mvi.common.data.exception

/**
 * Conflict exception
 * Always fatal as we couldn't retry with the same data
 */
data class ConflictException(override val message: String) : AppException() {
    override val isFatal: Boolean get() = true
}