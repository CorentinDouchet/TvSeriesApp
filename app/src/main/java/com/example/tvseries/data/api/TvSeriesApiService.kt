// data/api/TvSeriesApiService.kt
package com.example.tvseries.data.api

import com.example.tvseries.data.model.TvSeriesResponse
import com.example.tvseries.data.model.TvSeriesDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TvSeriesApiService {

    @GET("most-popular")
    suspend fun getMostPopularTvSeries(
        @Query("page") page: Int = 1
    ): Response<TvSeriesResponse>

    @GET("search")
    suspend fun searchTvSeries(
        @Query("q") query: String,
        @Query("page") page: Int = 1
    ): Response<TvSeriesResponse>

    @GET("show-details")
    suspend fun getTvSeriesDetails(
        @Query("q") permalink: String
    ): Response<TvSeriesDetailResponse>

    companion object {
        const val BASE_URL = "https://www.episodate.com/api/"
    }
}