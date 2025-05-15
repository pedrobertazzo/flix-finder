package com.flixfinder.model.dto

import com.flixfinder.model.Movie

data class UserBacklogRequest(
    val userId: Long,
    val movies: List<Movie>
)
