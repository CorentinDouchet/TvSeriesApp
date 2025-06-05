package com.example.tvseries.data.repository

import com.example.tvseries.data.api.TvSeriesApiService
import com.example.tvseries.data.model.TvSeriesResponse
import com.example.tvseries.data.model.TvSeriesDetailResponse
import com.example.tvseries.data.model.SearchFilter
import com.example.tvseries.domain.repository.TvSeriesRepository
import com.example.tvseries.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TvSeriesRepositoryImpl @Inject constructor(
    private val apiService: TvSeriesApiService
) : TvSeriesRepository {

    override suspend fun getMostPopularTvSeries(page: Int): Flow<Resource<TvSeriesResponse>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getMostPopularTvSeries(page)

            if (response.isSuccessful) {
                response.body()?.let { tvSeriesResponse ->
                    emit(Resource.Success(tvSeriesResponse))
                } ?: emit(Resource.Error("Données vides reçues"))
            } else {
                emit(Resource.Error("Erreur ${response.code()}: ${response.message()}"))
            }

        } catch (e: HttpException) {
            emit(Resource.Error("Erreur réseau: ${e.localizedMessage ?: "Erreur inconnue"}"))
        } catch (e: IOException) {
            emit(Resource.Error("Erreur de connexion: Vérifiez votre connexion internet"))
        } catch (e: Exception) {
            emit(Resource.Error("Erreur inattendue: ${e.localizedMessage ?: "Erreur inconnue"}"))
        }
    }

    override suspend fun searchTvSeries(query: String, page: Int): Flow<Resource<TvSeriesResponse>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.searchTvSeries(query, page)

            if (response.isSuccessful) {
                response.body()?.let { tvSeriesResponse ->
                    emit(Resource.Success(tvSeriesResponse))
                } ?: emit(Resource.Error("Aucun résultat trouvé"))
            } else {
                emit(Resource.Error("Erreur ${response.code()}: ${response.message()}"))
            }

        } catch (e: HttpException) {
            emit(Resource.Error("Erreur réseau: ${e.localizedMessage ?: "Erreur inconnue"}"))
        } catch (e: IOException) {
            emit(Resource.Error("Erreur de connexion: Vérifiez votre connexion internet"))
        } catch (e: Exception) {
            emit(Resource.Error("Erreur inattendue: ${e.localizedMessage ?: "Erreur inconnue"}"))
        }
    }

    override suspend fun getTvSeriesDetails(permalink: String): Flow<Resource<TvSeriesDetailResponse>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getTvSeriesDetails(permalink)

            if (response.isSuccessful) {
                response.body()?.let { detailResponse ->
                    emit(Resource.Success(detailResponse))
                } ?: emit(Resource.Error("Détails non trouvés"))
            } else {
                emit(Resource.Error("Erreur ${response.code()}: ${response.message()}"))
            }

        } catch (e: HttpException) {
            emit(Resource.Error("Erreur réseau: ${e.localizedMessage ?: "Erreur inconnue"}"))
        } catch (e: IOException) {
            emit(Resource.Error("Erreur de connexion: Vérifiez votre connexion internet"))
        } catch (e: Exception) {
            emit(Resource.Error("Erreur inattendue: ${e.localizedMessage ?: "Erreur inconnue"}"))
        }
    }

    override suspend fun searchWithFilters(filter: SearchFilter, page: Int): Flow<Resource<TvSeriesResponse>> = flow {
        try {
            emit(Resource.Loading())

            // Si on a une requête de recherche, on utilise l'endpoint de recherche
            val response = if (filter.query.isNotEmpty()) {
                apiService.searchTvSeries(filter.query, page)
            } else {
                // Sinon on récupère les séries populaires
                apiService.getMostPopularTvSeries(page)
            }

            if (response.isSuccessful) {
                response.body()?.let { tvSeriesResponse ->
                    // Si on a des filtres de genre, on filtre localement
                    val filteredShows = if (filter.selectedGenres.isNotEmpty()) {
                        // On devra récupérer les détails de chaque série pour filtrer par genre
                        // Pour l'instant, on retourne toutes les séries (limitation de l'API)
                        tvSeriesResponse
                    } else {
                        tvSeriesResponse
                    }
                    emit(Resource.Success(filteredShows))
                } ?: emit(Resource.Error("Aucun résultat trouvé"))
            } else {
                emit(Resource.Error("Erreur ${response.code()}: ${response.message()}"))
            }

        } catch (e: HttpException) {
            emit(Resource.Error("Erreur réseau: ${e.localizedMessage ?: "Erreur inconnue"}"))
        } catch (e: IOException) {
            emit(Resource.Error("Erreur de connexion: Vérifiez votre connexion internet"))
        } catch (e: Exception) {
            emit(Resource.Error("Erreur inattendue: ${e.localizedMessage ?: "Erreur inconnue"}"))
        }
    }
}