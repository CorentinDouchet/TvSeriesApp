// data/model/Genre.kt
package com.example.tvseries.data.model

data class Genre(
    val name: String,
    val isSelected: Boolean = false
) {
    companion object {
        // Genres les plus populaires basés sur les données EpisoDate
        val POPULAR_GENRES = listOf(
            "Action", "Adventure", "Animation", "Biography", "Comedy",
            "Crime", "Documentary", "Drama", "Family", "Fantasy",
            "History", "Horror", "Music", "Mystery", "Romance",
            "Science-Fiction", "Sport", "Supernatural", "Thriller", "War"
        )
    }
}