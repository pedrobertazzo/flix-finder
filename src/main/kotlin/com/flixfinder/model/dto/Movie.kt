package com.flixfinder.model.dto

interface BaseMovie {
    val title: String
    val releaseYear: Int
    val description: String
    val genre: Genre
}

data class Movie(
    override val title: String,
    override val releaseYear: Int,
    override val description: String,
    override val genre: Genre
) : BaseMovie

data class ComposedMovie(
    val movie: Movie,
    val imageUrl: String? = null
)
