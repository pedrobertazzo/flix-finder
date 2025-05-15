package com.flixfinder.service

import com.flixfinder.model.Movie

interface MovieRecommendationService {
    suspend fun getMovieRecommendations(preferences: String, genres: List<String>): List<Movie>
}
