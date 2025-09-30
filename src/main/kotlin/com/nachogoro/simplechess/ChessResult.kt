package com.nachogoro.simplechess

/**
 * Result type for chess library operations, following the Either pattern.
 *
 * This replaces exceptions in the original C++ interface with a more functional approach.
 */
public sealed class ChessResult<out T> {
    /**
     * Successful result containing a value.
     */
    public data class Success<T>(val value: T) : ChessResult<T>()

    /**
     * Error result containing an error message and optional cause.
     */
    public data class Error(val message: String, val cause: Throwable? = null) : ChessResult<Nothing>()

    /**
     * Returns true if this is a Success result.
     */
    public val isSuccess: Boolean get() = this is Success

    /**
     * Returns true if this is an Error result.
     */
    public val isError: Boolean get() = this is Error

    /**
     * Returns the value if this is a Success, or null if Error.
     */
    public fun getOrNull(): T? = when (this) {
        is Success -> value
        is Error -> null
    }

    /**
     * Returns the value if this is a Success, or throws the error if Error.
     */
    public fun getOrThrow(): T = when (this) {
        is Success -> value
        is Error -> throw RuntimeException(message, cause)
    }

    /**
     * Returns the value if this is a Success, or the default value if Error.
     */
    public fun getOrElse(default: @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Error -> default
    }

    public companion object {
        /**
         * Creates a Success result.
         */
        public fun <T> success(value: T): ChessResult<T> = Success(value)

        /**
         * Creates an Error result.
         */
        public fun <T> error(message: String, cause: Throwable? = null): ChessResult<T> = Error(message, cause)

        /**
         * Wraps a potentially throwing operation in a ChessResult.
         */
        public inline fun <T> catching(operation: () -> T): ChessResult<T> = try {
            Success(operation())
        } catch (e: Exception) {
            Error(e.message ?: "Unknown error", e)
        }
    }
}

/**
 * Transform the value inside a Success result, or pass through Error unchanged.
 */
public inline fun <T, R> ChessResult<T>.map(transform: (T) -> R): ChessResult<R> = when (this) {
    is ChessResult.Success -> ChessResult.Success(transform(value))
    is ChessResult.Error -> this
}

/**
 * Transform the error message if this is an Error, or pass through Success unchanged.
 */
public inline fun <T> ChessResult<T>.mapError(transform: (String) -> String): ChessResult<T> = when (this) {
    is ChessResult.Success -> this
    is ChessResult.Error -> ChessResult.Error(transform(message), cause)
}

/**
 * Execute a side effect if this is a Success, without changing the result.
 */
public inline fun <T> ChessResult<T>.onSuccess(action: (T) -> Unit): ChessResult<T> {
    if (this is ChessResult.Success) action(value)
    return this
}

/**
 * Execute a side effect if this is an Error, without changing the result.
 */
public inline fun <T> ChessResult<T>.onError(action: (String) -> Unit): ChessResult<T> {
    if (this is ChessResult.Error) action(message)
    return this
}