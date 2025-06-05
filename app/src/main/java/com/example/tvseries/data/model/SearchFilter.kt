// data/model/SearchFilter.kt
package com.example.tvseries.data.model

data class SearchFilter(
    val query: String = "",
    val selectedGenres: Set<String> = emptySet(),
    val isActive: Boolean = false
) {
    fun hasActiveFilters(): Boolean = query.isNotEmpty() || selectedGenres.isNotEmpty()
}