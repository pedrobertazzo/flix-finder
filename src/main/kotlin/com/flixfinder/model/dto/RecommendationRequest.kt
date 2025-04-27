package com.flixfinder.model

data class RecommendationRequest(
    val userPreferences: String,
    val genres: List<Genre> = emptyList(),
    val userId: Long?
)
