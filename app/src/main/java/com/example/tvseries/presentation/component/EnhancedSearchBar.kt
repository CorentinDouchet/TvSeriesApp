// presentation/component/EnhancedSearchBar.kt
package com.example.tvseries.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    suggestions: List<String> = emptyList(),
    recentSearches: List<String> = emptyList(),
    isSearchActive: Boolean,
    onToggleFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSuggestions by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { newQuery ->
                    onQueryChange(newQuery)
                    showSuggestions = newQuery.isNotEmpty()
                },
                placeholder = {
                    Text("Rechercher une série (ex: Breaking Bad, Game of Thrones...)")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Rechercher"
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onQueryChange("")
                                showSuggestions = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Effacer"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState ->
                        showSuggestions = focusState.isFocused &&
                                (query.isNotEmpty() || recentSearches.isNotEmpty())
                    },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            IconButton(
                onClick = onToggleFilters,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isSearchActive)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = "⚙️",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isSearchActive)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Suggestions et recherches récentes
        AnimatedVisibility(
            visible = showSuggestions,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    // Suggestions basées sur la requête actuelle
                    if (suggestions.isNotEmpty() && query.isNotEmpty()) {
                        item {
                            Text(
                                text = "Suggestions",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 8.dp)
                            )
                        }

                        items(suggestions) { suggestion ->
                            SuggestionItem(
                                text = suggestion,
                                query = query,
                                onClick = {
                                    onSearch(suggestion)
                                    showSuggestions = false
                                    focusManager.clearFocus()
                                }
                            )
                        }
                    }

                    // Recherches récentes
                    if (recentSearches.isNotEmpty() && query.isEmpty()) {
                        item {
                            Text(
                                text = "Recherches récentes",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 8.dp)
                            )
                        }

                        items(recentSearches.take(5)) { recentSearch ->
                            RecentSearchItem(
                                text = recentSearch,
                                onClick = {
                                    onSearch(recentSearch)
                                    showSuggestions = false
                                    focusManager.clearFocus()
                                }
                            )
                        }
                    }

                    // Message si aucune suggestion
                    if (suggestions.isEmpty() && query.length >= 2) {
                        item {
                            Text(
                                text = "Appuyez sur Entrée pour rechercher \"$query\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionItem(
    text: String,
    query: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Mettre en surbrillance la partie qui correspond à la requête
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun RecentSearchItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Utilisation d'un emoji ou texte au lieu d'une icône
        Text(
            text = "🕒",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Composant pour afficher des exemples de recherche
@Composable
fun SearchExamples(
    onExampleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val examples = listOf(
        "Breaking Bad", "Game of Thrones", "Stranger Things",
        "The Office", "Friends", "The Walking Dead"
    )

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "Exemples de recherche :",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        examples.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { example ->
                    AssistChip(
                        onClick = { onExampleClick(example) },
                        label = { Text(example) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}