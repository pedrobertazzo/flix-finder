package com.flixfinder.model.dto

data class UserBacklogRequest(
    val userId: Long,
    val movies: List<Movie>
)
