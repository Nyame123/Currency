package com.bismark.currency.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AnnotatedDispatchers(val dispatcher: CurrencyDispatcher) {
}

enum class CurrencyDispatcher {
    IO
}
