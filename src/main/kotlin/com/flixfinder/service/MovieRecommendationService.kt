package com.flixfinder.service

import com.flixfinder.model.Movie

interface MovieRecommendationService {
    fun getMovieRecommendation(preferences: String, genres: List<String>): List<Movie>
}
