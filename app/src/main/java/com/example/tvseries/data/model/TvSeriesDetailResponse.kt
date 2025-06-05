// data/model/TvSeriesDetailResponse.kt
package com.example.tvseries.data.model

import com.google.gson.annotations.SerializedName

data class TvSeriesDetailResponse(
    @SerializedName("tvShow")
    val tvShow: TvSeriesDetail
)

data class TvSeriesDetail(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("permalink")
    val permalink: String,

    @SerializedName("url")
    val url: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("description_source")
    val descriptionSource: String?,

    @SerializedName("start_date")
    val startDate: String?,

    @SerializedName("end_date")
    val endDate: String?,

    @SerializedName("country")
    val country: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("runtime")
    val runtime: Int?,

    @SerializedName("network")
    val network: String?,

    @SerializedName("youtube_link")
    val youtubeLink: String?,

    @SerializedName("image_path")
    val imagePath: String?,

    @SerializedName("image_thumbnail_path")
    val imageThumbnailPath: String?,

    @SerializedName("rating")
    val rating: String?,

    @SerializedName("rating_count")
    val ratingCount: String?,

    @SerializedName("countdown")
    val countdown: String?,

    @SerializedName("genres")
    val genres: List<String>?
)



