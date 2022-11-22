package com.bismark.currency.di

import com.bismark.currency.domain.usecase.NetworkConnectivity
import com.bismark.currency.domain.usecase.NetworkConnectivityImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkConnectivityModule {

    @Singleton
    @Binds
    abstract fun bindsNetworkConnectivity(networkConnectivityImpl: NetworkConnectivityImpl): NetworkConnectivity
}
