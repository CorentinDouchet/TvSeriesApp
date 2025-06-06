🎨 Palette de Couleurs Cohérente
kotlin// Theme personnalisé basé sur Material 3
object TvSeriesColors {
    val Background = Color(0xFF2D2D2D)      // Gris anthracite principal
    val Surface = Color(0xFF404040)         // Gris moyen pour les cartes
    val OnBackground = Color(0xFFFFFFFF)    // Texte principal blanc
    val OnSurface = Color(0xFFE0E0E0)       // Texte secondaire gris clair
    val Primary = Color(0xFF6750A4)         // Violet Material pour actions
    val Secondary = Color(0xFF958DA5)       // Violet désaturé pour éléments secondaires
    val Error = Color(0xFFBA1A1A)           // Rouge pour erreurs
}

@Composable
fun TvSeriesTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        background = TvSeriesColors.Background,
        surface = TvSeriesColors.Surface,
        onBackground = TvSeriesColors.OnBackground,
        onSurface = TvSeriesColors.OnSurface,
        primary = TvSeriesColors.Primary,
        error = TvSeriesColors.Error
    )
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = TvSeriesTypography,
        content = content
    )
}
🔤 Typographie Hiérarchisée
kotlinval TvSeriesTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.# 📺 TV Series App

Une application Android moderne développée en **Kotlin** avec **Jetpack Compose** pour naviguer et rechercher dans une base de données de séries TV. L'application utilise l'architecture **MVVM** et **Dagger Hilt** pour l'injection de dépendances.

## 🚀 Fonctionnalités Principales

- **Navigation des séries TV** avec pagination infinie
- **Recherche avancée** par titre avec filtres temps réel
- **Filtrage par genres** avec sélection multiple
- **Interface moderne** en Material Design 3
- **Gestion d'état robuste** avec StateFlow
- **Architecture modulaire** et testable

## 🏗️ Architecture

### Structure du Projet (Clean Architecture)
com.example.tvseries/
├── data/                    # Couche de données (Frameworks & Drivers)
│   ├── api/                # Interfaces API et DTOs
│   │   ├── TvSeriesApi.kt  # Définitions Retrofit
│   │   └── dto/            # Data Transfer Objects
│   ├── repository/         # Implémentations des repositories
│   │   └── TvSeriesRepositoryImpl.kt
│   └── di/                 # Modules d'injection Dagger Hilt
│       └── NetworkModule.kt
├── domain/                 # Couche métier (Use Cases & Entities)
│   ├── model/              # Modèles de domaine (Business Objects)
│   │   ├── TvSeries.kt     # Entité principale
│   │   └── Genre.kt        # Entité genre
│   ├── repository/         # Interfaces des repositories (Contracts)
│   │   └── TvSeriesRepository.kt
│   └── usecase/            # Cas d'usage métier (Business Rules)
│       ├── GetTvSeriesUseCase.kt
│       └── SearchTvSeriesUseCase.kt
├── presentation/           # Couche présentation (UI & Controllers)
│   ├── screen/             # Écrans Compose
│   │   └── TvSeriesScreen.kt
│   ├── component/          # Composants UI réutilisables
│   │   ├── SearchBar.kt
│   │   ├── TvSeriesGrid.kt
│   │   └── GenreFilterPanel.kt
│   ├── viewmodel/          # ViewModels (Presentation Logic)
│   │   └── TvSeriesViewModel.kt
│   └── state/              # États UI et modèles de données
│       ├── PaginationState.kt
│       └── SearchFilter.kt
└── di/                     # Configuration Dagger Hilt globale
└── ApplicationModule.kt

### Principles SOLID Appliqués

**🔹 Single Responsibility Principle (SRP)**
- Chaque classe a une responsabilité unique
- `TvSeriesRepository` : uniquement accès aux données
- `SearchTvSeriesUseCase` : uniquement logique de recherche

**🔹 Open/Closed Principle (OCP)**
- Extensions possibles sans modification du code existant
- Nouveaux filtres ajoutables via interface `Filter`

**🔹 Liskov Substitution Principle (LSP)**
- Implémentations interchangeables via interfaces
- `TvSeriesRepositoryImpl` peut être remplacée par `TvSeriesRepositoryMock`

**🔹 Interface Segregation Principle (ISP)**
- Interfaces spécialisées et focalisées
- `SearchRepository` séparée de `TvSeriesRepository`

**🔹 Dependency Inversion Principle (DIP)**
- Dépendances vers les abstractions, pas les implémentations
- `ViewModel` dépend de `UseCase` interface, pas implémentation

### Patterns Architecturaux Détaillés

**🎯 MVVM (Model-View-ViewModel)**
┌─────────────┐    observe    ┌─────────────┐    calls    ┌─────────────┐
│    View     │ ──────────────→│ ViewModel   │ ────────────→│   Model     │
│ (Compose)   │                │ (StateFlow) │             │ (Use Cases) │
└─────────────┘                └─────────────┘             └─────────────┘

**🏛️ Clean Architecture - Flux de Dépendances**
Presentation Layer ──→ Domain Layer ←── Data Layer
(UI Logic)        (Business Logic)    (Data Access)

**🔄 Repository Pattern Avancé**
```kotlin
interface TvSeriesRepository {
    suspend fun getTvSeries(page: Int): Result<List<TvSeries>>
    suspend fun searchTvSeries(query: String, page: Int): Result<List<TvSeries>>
}

class TvSeriesRepositoryImpl @Inject constructor(
    private val api: TvSeriesApi,           // Source distante
    private val cache: TvSeriesCache        // Source locale
) : TvSeriesRepository {
    
    override suspend fun getTvSeries(page: Int): Result<List<TvSeries>> {
        return try {
            // 1. Vérifier le cache
            val cached = cache.getTvSeries(page)
            if (cached.isNotEmpty() && !cache.isExpired(page)) {
                return Result.success(cached)
            }
            
            // 2. Appel API si cache vide/expiré
            val response = api.getTvSeries(page)
            
            // 3. Mise à jour du cache
            cache.saveTvSeries(page, response.toDomainModel())
            
            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
💉 Dependency Injection avec Hilt
kotlin@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    @Provides
    @Singleton
    fun provideTvSeriesApi(retrofit: Retrofit): TvSeriesApi =
        retrofit.create(TvSeriesApi::class.java)
}
🔧 Technologies Utilisées
TechnologieVersionUsageAvantages ClésKotlin1.9+Langage principal• Null safety<br>• Concision du code<br>• Interopérabilité JavaJetpack ComposeLatestInterface utilisateur déclarative• UI réactive<br>• Moins de boilerplate<br>• Prévisualisations temps réelMaterial 3LatestDesign system moderne• Cohérence visuelle<br>• Thème adaptatif<br>• Composants standardisésDagger Hilt2.48+Injection de dépendances• Réduction du couplage<br>• Tests simplifiés<br>• Performance optimiséeRetrofit2.9+Client HTTP pour API REST• Sérialisation automatique<br>• Interceptors<br>• Gestion d'erreursStateFlowLatestGestion d'état réactive• État partagé sûr<br>• Lifecycle-aware<br>• Backpressure handlingCoroutines1.7+Programmation asynchrone• Code séquentiel<br>• Gestion mémoire<br>• Cancellation cooperative
📱 Fonctions Principales
1. TvSeriesScreen - Écran Principal
kotlin@Composable
fun TvSeriesScreen(viewModel: TvSeriesViewModel = hiltViewModel())
But : Écran principal affichant la grille de séries avec recherche et filtres.
Fonctionnalités :

Affichage en grille des séries TV
Barre de recherche intégrée
Panel de filtres par genres
Pagination infinie
Gestion des états de chargement et d'erreur

2. TvSeriesViewModel - Gestion d'État
kotlinclass TvSeriesViewModel @Inject constructor(
    private val getTvSeriesUseCase: GetTvSeriesUseCase,
    private val searchTvSeriesUseCase: SearchTvSeriesUseCase
) : ViewModel()
But : Gère l'état de l'écran et orchestre les interactions entre l'UI et la couche métier.
Architecture MVVM Détaillée :

Séparation claire : UI ne communique qu'avec le ViewModel
Survie aux changements de configuration (rotation d'écran)
Gestion centralisée de tous les états de l'écran

Responsabilités :

loadTvSeries() : Charge la liste initiale des séries
kotlinfun loadTvSeries(isRefresh: Boolean = false) {
    viewModelScope.launch {
        _paginationState.value = _paginationState.value.copy(isLoading = true)
        // Logique de chargement avec gestion d'erreurs
    }
}

loadMoreTvSeries() : Implémente la pagination infinie

Détection automatique de la fin de liste
Prévention des appels redondants
Gestion du cache et de la mémoire


searchTvSeries(query: String) : Recherche par titre avec debounce

Debounce de 300ms pour éviter les appels excessifs
Annulation automatique des recherches précédentes
Combinaison recherche + filtres genres


toggleGenre(genreName: String) : Active/désactive un filtre de genre
clearGenreFilters() : Remet à zéro tous les filtres

États Exposés (Pattern Observer) :
kotlinval tvSeriesList: StateFlow<List<TvSeries>>        // Liste réactive des séries
val paginationState: StateFlow<PaginationState>   // État de pagination
val searchFilter: StateFlow<SearchFilter>         // Filtres actifs
val availableGenres: StateFlow<List<Genre>>       // Genres disponibles
val isSearchMode: StateFlow<Boolean>              // Mode recherche actif
Avantages de StateFlow vs LiveData :

Thread-safe par défaut
Coroutines-first design
Conflation automatique (évite les émissions redondantes)
Initial value obligatoire (pas de null states)

3. SearchBar - Composant de Recherche
kotlin@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    isSearchActive: Boolean,
    onToggleFilters: () -> Unit
)
But : Barre de recherche avec détection temps réel et bouton de filtres.
Fonctionnalités :

Recherche instantanée avec debounce (300ms)
Indication visuelle de l'état actif
Bouton d'accès aux filtres par genre
Gestion du focus et du clavier

4. TvSeriesGrid - Grille d'Affichage
kotlin@Composable
fun TvSeriesGrid(
    tvSeries: List<TvSeries>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasMorePages: Boolean,
    onLoadMore: () -> Unit
)
But : Affiche les séries dans une grille responsive avec pagination infinie.
Optimisations de Performance :

LazyVerticalGrid : Virtualisation automatique (seuls les éléments visibles sont rendus)
key() parameter : Évite les recompositions inutiles lors des mises à jour
Stable parameters : Utilisation de data classes immutables
remember() pour les calculs coûteux

Fonctionnalités Avancées :

Grille adaptive (2-3 colonnes selon la taille d'écran)
kotlincolumns = GridCells.Adaptive(minSize = 150.dp) // Responsive design

Détection automatique du scroll vers le bas
kotlin// Détection du dernier élément visible
LaunchedEffect(listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index) {
    if (shouldLoadMore) onLoadMore()
}

Indicateur de chargement en bas de liste
Gestion gracieuse des erreurs avec retry automatique

Pagination Infinie Détaillée :

Seuil de déclenchement : 3 éléments avant la fin
Protection contre les doublons via état de loading
Cache intelligent : garde les données précédentes
Gestion mémoire : limite automatique du cache

5. GenreFilterPanel - Filtres par Genre
kotlin@Composable
fun GenreFilterPanel(
    genres: List<Genre>,
    onToggleGenre: (String) -> Unit,
    onClearFilters: () -> Unit,
    isVisible: Boolean
)
But : Panel coulissant pour filtrer les séries par genres.
Fonctionnalités :

Sélection multiple de genres
Animation d'ouverture/fermeture
Bouton de remise à zéro
Chips Material Design pour chaque genre

6. PaginationState - Gestion de la Pagination
kotlindata class PaginationState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMorePages: Boolean = true,
    val currentPage: Int = 1,
    val error: String? = null
)
But : Encapsule l'état de la pagination pour une gestion propre du chargement.
Utilisations :

Contrôle de l'affichage des indicateurs de chargement
Prévention des appels API redondants
Gestion des erreurs de réseau

7. SearchFilter - État des Filtres
kotlindata class SearchFilter(
    val query: String = "",
    val selectedGenres: Set<String> = emptySet()
) {
    fun hasActiveFilters(): Boolean = query.isNotEmpty() || selectedGenres.isNotEmpty()
}
But : Centralise la logique de filtrage et de recherche.
Fonctionnalités :

Détection automatique des filtres actifs
Combinaison recherche texte + genres
État immutable pour une gestion sûre
