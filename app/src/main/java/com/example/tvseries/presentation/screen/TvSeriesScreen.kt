// presentation/screen/TvSeriesScreen.kt
package com.example.tvseries.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tvseries.presentation.component.*
import com.example.tvseries.presentation.viewmodel.TvSeriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvSeriesScreen(
    viewModel: TvSeriesViewModel = hiltViewModel()
) {
    val tvSeriesList by viewModel.tvSeriesList.collectAsState()
    val paginationState by viewModel.paginationState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchFilter by viewModel.searchFilter.collectAsState()
    val availableGenres by viewModel.availableGenres.collectAsState()
    val isSearchMode by viewModel.isSearchMode.collectAsState()

    var showFilters by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Fond gris clair
    ) {
        // Titre centré (remplace la TopAppBar)
        Text(
            text = "TV Series",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        // Barre de recherche
        SearchBar(
            query = searchFilter.query,
            onQueryChange = { query ->
                viewModel.searchTvSeries(query)
            },
            onSearch = { query ->
                viewModel.searchTvSeries(query)
            },
            isSearchActive = searchFilter.hasActiveFilters(),
            onToggleFilters = { showFilters = !showFilters }
        )

        // Panel de filtres par genre
        GenreFilterPanel(
            genres = availableGenres,
            onToggleGenre = { genreName ->
                viewModel.toggleGenre(genreName)
            },
            onClearFilters = {
                viewModel.clearGenreFilters()
            },
            isVisible = showFilters
        )

        // Informations sur les résultats de recherche (sans sous-titre)
        if (isSearchMode && searchFilter.query.isNotEmpty()) {
            SearchResultsInfo(
                resultsCount = tvSeriesList.size,
                searchQuery = searchFilter.query,
                selectedGenres = searchFilter.selectedGenres,
                onClearSearch = {
                    viewModel.loadTvSeries(isRefresh = true)
                    showFilters = false
                }
            )
        }

        // Contenu principal
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                // État de chargement initial
                paginationState.isLoading && tvSeriesList.isEmpty() -> {
                    LoadingIndicator()
                }

                // Liste vide après recherche
                tvSeriesList.isEmpty() && isSearchMode && !paginationState.isLoading -> {
                    EmptySearchResults(
                        query = searchFilter.query,
                        selectedGenres = searchFilter.selectedGenres,
                        onClearSearch = {
                            viewModel.loadTvSeries(isRefresh = true)
                            showFilters = false
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Affichage des données
                else -> {
                    TvSeriesGrid(
                        tvSeries = tvSeriesList,
                        isLoading = paginationState.isLoading,
                        isLoadingMore = paginationState.isLoadingMore,
                        hasMorePages = paginationState.hasMorePages,
                        onLoadMore = { viewModel.loadMoreTvSeries() }
                    )
                }
            }

            // Affichage des erreurs (sans bouton retry)
            paginationState.error?.let { error ->
                if (tvSeriesList.isEmpty()) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptySearchResults(
    query: String,
    selectedGenres: Set<String>,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔍",
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Aucun résultat trouvé",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (query.isNotEmpty()) {
            Text(
                text = "pour \"$query\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (selectedGenres.isNotEmpty()) {
            Text(
                text = "avec les genres: ${selectedGenres.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onClearSearch
        ) {
            Text("Voir toutes les séries")
        }
    }
}