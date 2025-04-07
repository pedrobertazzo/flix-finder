package com.flixfinder.service.impl

import com.flixfinder.model.Movie
import com.flixfinder.service.api.MovieRecommendationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient


@Service
class MovieRecommendationImpl(
    @Value("\${openai.apiKey}") private val apiKey: String,
    private val webClientBuilder: WebClient.Builder,
) : MovieRecommendationService {

    private val client = webClientBuilder
        .baseUrl("https://api.themoviedb.org/3")
        .build()

    override fun searchMovies(query: String): List<Movie> {
        // TODO: get movies from external API.
        return listOf()
    };

    override fun getMovieRecommendation(query: String): String {
        TODO("Not yet implemented")
    }
}