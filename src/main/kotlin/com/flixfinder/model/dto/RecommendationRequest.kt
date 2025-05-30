package com.flixfinder.model.dto

data class RecommendationRequest(
    val userPreferences: String,
    val genres: List<Genre> = emptyList()
)
