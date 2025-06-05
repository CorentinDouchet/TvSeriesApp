// presentation/viewmodel/TvSeriesViewModel.kt
package com.example.tvseries.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvseries.data.model.Genre
import com.example.tvseries.data.model.PaginationState
import com.example.tvseries.data.model.SearchFilter
import com.example.tvseries.data.model.TvSeries
import com.example.tvseries.domain.repository.TvSeriesRepository
import com.example.tvseries.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvSeriesViewModel @Inject constructor(
    private val repository: TvSeriesRepository
) : ViewModel() {

    private val _tvSeriesList = MutableStateFlow<List<TvSeries>>(emptyList())
    val tvSeriesList: StateFlow<List<TvSeries>> = _tvSeriesList.asStateFlow()

    private val _paginationState = MutableStateFlow(PaginationState())
    val paginationState: StateFlow<PaginationState> = _paginationState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchFilter = MutableStateFlow(SearchFilter())
    val searchFilter: StateFlow<SearchFilter> = _searchFilter.asStateFlow()

    private val _availableGenres = MutableStateFlow(
        Genre.POPULAR_GENRES.map { Genre(it, false) }
    )
    val availableGenres: StateFlow<List<Genre>> = _availableGenres.asStateFlow()

    private val _isSearchMode = MutableStateFlow(false)
    val isSearchMode: StateFlow<Boolean> = _isSearchMode.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadTvSeries()
    }

    fun loadTvSeries(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _tvSeriesList.value = emptyList()
                _paginationState.value = PaginationState()
                _errorMessage.value = null
                _isSearchMode.value = false
                _searchFilter.value = SearchFilter()
            }

            val currentPage = if (isRefresh) 1 else _paginationState.value.currentPage

            repository.getMostPopularTvSeries(currentPage).collect { resource ->
                handleResource(resource, isRefresh || currentPage == 1)
            }
        }
    }

    fun searchTvSeries(query: String) {
        searchJob?.cancel()

        _searchFilter.value = _searchFilter.value.copy(query = query)

        if (query.isEmpty()) {
            _isSearchMode.value = false
            loadTvSeries(isRefresh = true)
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce

            _isSearchMode.value = true
            _tvSeriesList.value = emptyList()
            _paginationState.value = PaginationState()
            _errorMessage.value = null

            repository.searchWithFilters(_searchFilter.value, 1).collect { resource ->
                handleResource(resource, true)
            }
        }
    }

    fun toggleGenre(genreName: String) {
        val updatedGenres = _availableGenres.value.map { genre ->
            if (genre.name == genreName) {
                genre.copy(isSelected = !genre.isSelected)
            } else {
                genre
            }
        }
        _availableGenres.value = updatedGenres

        val selectedGenres = updatedGenres.filter { it.isSelected }.map { it.name }.toSet()
        _searchFilter.value = _searchFilter.value.copy(selectedGenres = selectedGenres)

        // Appliquer les filtres
        applyFilters()
    }

    fun clearGenreFilters() {
        _availableGenres.value = _availableGenres.value.map { it.copy(isSelected = false) }
        _searchFilter.value = _searchFilter.value.copy(selectedGenres = emptySet())
        applyFilters()
    }

    private fun applyFilters() {
        viewModelScope.launch {
            _tvSeriesList.value = emptyList()
            _paginationState.value = PaginationState()
            _errorMessage.value = null

            val hasFilters = _searchFilter.value.hasActiveFilters()
            _isSearchMode.value = hasFilters

            if (hasFilters) {
                repository.searchWithFilters(_searchFilter.value, 1).collect { resource ->
                    handleResource(resource, true)
                }
            } else {
                loadTvSeries(isRefresh = true)
            }
        }
    }

    fun loadMoreTvSeries() {
        val currentState = _paginationState.value

        if (!currentState.isLoadingMore &&
            currentState.hasMorePages &&
            !currentState.isLoading) {

            viewModelScope.launch {
                val nextPage = currentState.currentPage + 1

                val flow = if (_isSearchMode.value) {
                    repository.searchWithFilters(_searchFilter.value, nextPage)
                } else {
                    repository.getMostPopularTvSeries(nextPage)
                }

                flow.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _paginationState.value = currentState.copy(isLoadingMore = true)
                        }

                        is Resource.Success -> {
                            resource.data?.let { response ->
                                _tvSeriesList.value = _tvSeriesList.value + response.tvShows
                                _paginationState.value = _paginationState.value.copy(
                                    isLoadingMore = false,
                                    currentPage = response.page,
                                    totalPages = response.pages,
                                    hasMorePages = response.page < response.pages,
                                    error = null
                                )
                            }
                        }

                        is Resource.Error -> {
                            _paginationState.value = _paginationState.value.copy(
                                isLoadingMore = false,
                                error = resource.message
                            )
                            _errorMessage.value = resource.message
                        }
                    }
                }
            }
        }
    }

    private fun handleResource(resource: Resource<com.example.tvseries.data.model.TvSeriesResponse>, isInitialLoad: Boolean) {
        when (resource) {
            is Resource.Loading -> {
                _paginationState.value = _paginationState.value.copy(
                    isLoading = isInitialLoad,
                    isLoadingMore = !isInitialLoad
                )
            }

            is Resource.Success -> {
                resource.data?.let { response ->
                    val newSeries = if (isInitialLoad) {
                        response.tvShows
                    } else {
                        _tvSeriesList.value + response.tvShows
                    }

                    _tvSeriesList.value = newSeries
                    _paginationState.value = _paginationState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        currentPage = response.page,
                        totalPages = response.pages,
                        hasMorePages = response.page < response.pages,
                        error = null
                    )
                }
            }

            is Resource.Error -> {
                _paginationState.value = _paginationState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = resource.message
                )
                _errorMessage.value = resource.message
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
        _paginationState.value = _paginationState.value.copy(error = null)
    }

    fun retry() {
        if (_isSearchMode.value) {
            applyFilters()
        } else {
            loadTvSeries()
        }
    }

// Améliorations pour la recherche par nom dans TvSeriesViewModel.kt

// Ajoutez cette méthode améliorée dans TvSeriesViewModel :

    fun searchTvSeriesImproved(query: String) {
        searchJob?.cancel()

        _searchFilter.value = _searchFilter.value.copy(query = query.trim())

        if (query.trim().isEmpty()) {
            _isSearchMode.value = false
            loadTvSeries(isRefresh = true)
            return
        }

        // Recherche immédiate si la requête est assez longue
        if (query.trim().length >= 2) {
            searchJob = viewModelScope.launch {
                delay(300) // Debounce de 300ms

                _isSearchMode.value = true
                _tvSeriesList.value = emptyList()
                _paginationState.value = PaginationState()
                _errorMessage.value = null

                repository.searchTvSeries(query.trim(), 1).collect { resource ->
                    handleResource(resource, true)
                }
            }
        }
    }

    // Méthode pour obtenir des suggestions pendant la frappe
    fun getSearchSuggestions(query: String, onSuggestions: (List<String>) -> Unit) {
        if (query.length >= 2) {
            viewModelScope.launch {
                try {
                    repository.searchTvSeries(query, 1).collect { resource ->
                        if (resource is Resource.Success) {
                            val suggestions = resource.data?.tvShows
                                ?.take(5) // Limiter à 5 suggestions
                                ?.map { it.name }
                                ?: emptyList()
                            onSuggestions(suggestions)
                        }
                    }
                } catch (e: Exception) {
                    onSuggestions(emptyList())
                }
            }
        }
    }
}

