package com.flixfinder.model

data class RecommendationRequest(
    val userPreferences: String,
    val genres: List<String> = emptyList()
)
