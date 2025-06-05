// data/model/TvSeriesResponse.kt
package com.example.tvseries.data.model

import com.google.gson.annotations.SerializedName

data class TvSeriesResponse(
    @SerializedName("total")
    val total: String,

    @SerializedName("page")
    val page: Int,

    @SerializedName("pages")
    val pages: Int,

    @SerializedName("tv_shows")
    val tvShows: List<TvSeries>
)