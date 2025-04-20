package com.flixfinder.service.api

interface MovieRecommendationService {
    fun getMovieRecommendation(preferences: String, genres: List<String>): String
}
