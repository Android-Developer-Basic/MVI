package ru.otus.mvi.common.data.exception

/**
 * Application exception
 */
sealed class AppException : RuntimeException() {
    /**
     * Fatal application exception if [isFatal] is true, otherwise non-fatal and could be recovered from
     */
    abstract val isFatal: Boolean

    /**
     * Message to show
     */
    abstract override val message: String
}

/**
 * Checks if exception is AppException or creates [UnknownException]
 */
fun Throwable.toAppException(): AppException = this as? AppException ?: UnknownException(message ?: "UNKNOWN", this)