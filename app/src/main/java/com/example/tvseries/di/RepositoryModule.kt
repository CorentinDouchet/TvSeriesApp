// di/RepositoryModule.kt
package com.example.tvseries.di

import com.example.tvseries.data.repository.TvSeriesRepositoryImpl
import com.example.tvseries.domain.repository.TvSeriesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTvSeriesRepository(
        tvSeriesRepositoryImpl: TvSeriesRepositoryImpl
    ): TvSeriesRepository
}