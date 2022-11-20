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

// Credits to Alex Hart -> https://proandroiddev.com/kotlins-nothing-type-946de7d464fb
// Composes 2 functions
fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C = {
    f(this(it))
}

/**
 * Binds the given function across [Either.Right].
 *
 * @param f The function to bind across [Either.Right].
 */
inline fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> =
    when (this) {
        is Either.Right -> f(this.b)
        is Either.Left -> this
    }

fun <T, L, R> Either<L, R>.map(fn: (R) -> (T)): Either<L, T> = this.flatMap(fn.c(::right))

/**
 * Perform the function lambda when result is successful
 */
suspend fun <L, R> Either<L, R>.onSuccessSuspended(fn: suspend (success: R) -> Unit): Either<L, R> =
    this.apply { if (this is Either.Right) fn(b) }

/**
 * Perform the function lambda when the result is failed
 */
suspend fun <L, R> Either<L, R>.onFailureSuspended(fn: suspend (failure: L) -> Unit): Either<L, R> =
    this.apply { if (this is Either.Left) fn(a) }
