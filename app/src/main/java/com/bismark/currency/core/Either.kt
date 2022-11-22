package com.bismark.currency.core

sealed class Either<out L, out R> {
    /** * Represents the left side of [Either] class which by convention is a "Failure". */
    data class Left<out L>(val a: L) : Either<L, Nothing>()

    /** * Represents the right side of [Either] class which by convention is a "Success". */
    data class Right<out R>(val b: R) : Either<Nothing, R>()

    val isRight get() = this is Right<R>
    val isLeft get() = this is Left<L>

    fun <L> left(a: L) = Left(a)
    fun <R> right(b: R) = Right(b)
}

/**
 * Unwrap [Either] and return the left value or null if [Either] is right
 */
fun <T> Either<T, Any>.leftOrNull(): T? = when (this) {
    is Either.Left -> a
    is Either.Right -> null
}

/**
 * Unwrap [Either] and return the right value or null if [Either] is left
 */
fun <T> Either<Any, T>.rightOrNull(): T? = when (this) {
    is Either.Right -> b
    is Either.Left -> null
}
