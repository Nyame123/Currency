package com.bismark.currency.di

import com.bismark.currency.data.ConversionRateRepositoryImpl
import com.bismark.currency.data.datasource.RemoteDataSource
import com.bismark.currency.data.datasource.RemoteDataSourceImpl
import com.bismark.currency.domain.ConversionRateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindsRemoteDataSource(remoteDataSource: RemoteDataSourceImpl): RemoteDataSource

    @Binds
    abstract fun bindsConversionRateRepository(conversionRateRepository: ConversionRateRepositoryImpl): ConversionRateRepository
}
