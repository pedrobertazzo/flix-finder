package com.flixfinder.model

data class RecommendationRequest(
    val userPreferences: String,
    val genres: List<Genre> = emptyList()
)

enum class Genre {
    HORROR, ROMANCE, COMEDY, ACTION, THRILLER, DRAMA, MYSTERY, FANTASY, SCIENCE_FICTION, ADVENTURE
}