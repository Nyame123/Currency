package com.bismark.currency.core

interface IDatabaseRequest {
    suspend fun <R> onSuspendCall(call: suspend ()-> R): Either<Failure,R> = try {
        Either.Right(call())
    }catch (exception: Exception){
        Either.Left(Failure.ServerConnectionError(exception))
    }
}
