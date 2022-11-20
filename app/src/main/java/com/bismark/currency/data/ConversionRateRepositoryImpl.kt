package com.bismark.currency.data

import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.core.map
import com.bismark.currency.core.onFailureSuspended
import com.bismark.currency.core.onSuccessSuspended
import com.bismark.currency.data.database.entities.ConversionResultEntity
import com.bismark.currency.data.datasource.local.LocalDataSource
import com.bismark.currency.data.datasource.remote.RemoteDataSource
import com.bismark.currency.domain.ConversionRateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ConversionRateRepositoryImpl(
    val localDataSource: LocalDataSource,
    val remoteDataSource: RemoteDataSource
) : ConversionRateRepository {

    override suspend fun fetchConversionRate(
        from: String,
        to: String,
        amount: Long
    ): Flow<Either<Failure, ConversionResultEntity>> = flow {
        val conversionResultRaw =
            remoteDataSource.fetchConversionRate(from = from, to = to, amount = amount)
                .onSuccessSuspended { success ->
                    localDataSource.insertConversionRate(success.asEntity()).onFailureSuspended { failure ->
                        //emits the error when local saving of the record fails
                        emit(Either.Left(failure))
                    }
                }.onFailureSuspended { failure ->
                    //emits the error when remote fetching of the conversion rate fails
                    emit(Either.Left(failure))
                }

        //Finally emits the conversion rate when fetching of the rate is successful
        emit(conversionResultRaw.map { it.asEntity() })
    }

    override suspend fun fetchLastThreeDaysHistory(
        startDate: Long,
        endDate: Long
    ): Flow<Either<Failure, List<ConversionResultEntity>>> = flow {
        emit(localDataSource.fetchLastThreeDaysHistory(startDate, endDate))
    }
}
