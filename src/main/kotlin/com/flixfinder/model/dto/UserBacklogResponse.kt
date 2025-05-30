package com.flixfinder.model.dto

data class UserBacklogResponse(
    val userId: Long,
    val movies: List<BacklogItem>
)

data class BacklogItem(
    val backlogItemId: Long?,
    val movie: Movie
)
