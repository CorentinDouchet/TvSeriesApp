// data/model/UiState.kt
package com.example.tvseries.data.model

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// Classe pour gérer l'état de la pagination
data class PaginationState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val hasMorePages: Boolean = true,
    val error: String? = null
)