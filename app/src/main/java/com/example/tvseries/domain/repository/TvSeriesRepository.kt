package com.example.tvseries.domain.repository

import com.example.tvseries.data.model.TvSeriesResponse
import com.example.tvseries.data.model.TvSeriesDetailResponse
import com.example.tvseries.data.model.SearchFilter
import com.example.tvseries.utils.Resource
import kotlinx.coroutines.flow.Flow

interface TvSeriesRepository {
    suspend fun getMostPopularTvSeries(page: Int): Flow<Resource<TvSeriesResponse>>
    suspend fun searchTvSeries(query: String, page: Int): Flow<Resource<TvSeriesResponse>>
    suspend fun getTvSeriesDetails(permalink: String): Flow<Resource<TvSeriesDetailResponse>>
    suspend fun searchWithFilters(filter: SearchFilter, page: Int): Flow<Resource<TvSeriesResponse>>
}