// presentation/screen/TvSeriesScreen.kt
package com.example.tvseries.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isSearchMode) "Recherche" else "Séries TV Populaires"
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.loadTvSeries(isRefresh = true)
                            showFilters = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualiser"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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

                // Affichage des erreurs
                paginationState.error?.let { error ->
                    if (tvSeriesList.isEmpty()) {
                        ErrorMessage(
                            message = error,
                            onRetry = { viewModel.retry() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
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