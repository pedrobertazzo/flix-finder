package com.flixfinder.service

import com.flixfinder.model.dto.ComposedMovie

interface MovieRecommendationService {
    suspend fun getMovieRecommendations(preferences: String, genres: List<String>): List<ComposedMovie>
}
