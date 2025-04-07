package com.flixfinder.service.api

import com.flixfinder.model.Movie

interface MovieRecommendationService {
    fun searchMovies(query: String): List<Movie>
    fun getMovieRecommendation(query: String): String
}