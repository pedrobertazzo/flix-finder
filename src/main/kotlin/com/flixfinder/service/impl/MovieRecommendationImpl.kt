package com.flixfinder.service.impl

import com.flixfinder.model.dto.ComposedMovie
import com.flixfinder.model.dto.Genre
import com.flixfinder.model.dto.Movie
import com.flixfinder.service.MovieRecommendationService
import com.google.gson.JsonParser
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatLanguageModel
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class MovieRecommendationImpl(
    private val chatLanguageModel: ChatLanguageModel,
    private val tmdbWebClient: WebClient
) : MovieRecommendationService {

    override suspend fun getMovieRecommendations(
        preferences: String,
        genres: List<String>
    ): List<ComposedMovie> {
        var retryCount = 3
        val prompt = """
        You are a movie recommendation assistant, suggest a max of 12 movies based on the following user query:
        "$preferences"
        For the following genres: ${genres.joinToString(", ")}

        Respond ONLY with a valid JSON array of movie objects with the following structure:
        [
          {
            "title": "Movie Title",
            "releaseYear": 2023,
            "description": "Movie Description (keep it short and in a single line/sentence)",
            "genre": "Horror"
          }
        ]

        The possible genre values are: ${Genre.entries.joinToString(", ")}

        Provide nothing but this JSON array in your response. Never add any other text or comments.
        The movie must exist; in case there is nothing that matches the preferences and genre, return an empty JSON array.
        When discussing the best or worst movies, take into consideration the ratings from professional critics.
        """.trimIndent()

        val messages = listOf(UserMessage(prompt))

        while (retryCount > 0) {
            try {
                val response = chatLanguageModel.generate(messages)
                val movies = parseMovies(response.content().text())

                return movies.map { movie ->
                    val imageUrl = fetchMoviePosterUrl(movie.title, movie.releaseYear)
                        .awaitFirstOrNull()
                    ComposedMovie(movie, imageUrl)
                }
            } catch (e: Exception) {
                retryCount--
                if (retryCount > 0) {
                    println("Error occurred, retrying (${3 - retryCount}/3): ${e.message}")
                } else {
                    throw RuntimeException("Failed to generate movie recommendations after 3 attempts: ${e.message}")
                }
            }
        }
        throw RuntimeException("Failed to generate movie recommendations")
    }

    private fun parseMovies(movieJsonResponse: String): List<Movie> {
        println("Parsing JSON array from openAI: $movieJsonResponse")

        // Clean up the response by removing Markdown code block syntax
        val cleanJson = movieJsonResponse
            .replace("```json", "")
            .replace("```", "")
            .trim()

        try {
            val jsonArray = JsonParser.parseString(cleanJson).asJsonArray

            return jsonArray.map { jsonElement ->
                val obj = jsonElement.asJsonObject
                val genreString = obj.get("genre").asString
                Movie(
                    title = obj.get("title").asString,
                    releaseYear = obj.get("releaseYear").asInt,
                    description = obj.get("description").asString,
                    genre = Genre.valueOf(genreString.uppercase())
                )
            }
        } catch (e: Exception) {
            println("Invalid JSON format from openAI: $movieJsonResponse\n${e.message}")
            throw RuntimeException("Failed to parse movie recommendations: ${e.message}")
        }
    }

    private fun fetchMoviePosterUrl(title: String, year: Int): Mono<String> {
        return tmdbWebClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/search/movie")
                    .queryParam("query", title)
                    .build()
            }
            .retrieve()
            .onStatus({ it.isError }) { response ->
                response.bodyToMono(String::class.java).flatMap { errorBody ->
                    println("TMDb error for $title ($year): $errorBody")
                    Mono.error(RuntimeException("TMDb error: ${response.statusCode()}"))
                }
            }
            .bodyToMono(String::class.java)
            .mapNotNull { json ->
                val jsonResponse = JsonParser.parseString(json).asJsonObject
                val results = jsonResponse.getAsJsonArray("results")
                val firstResult = results?.firstOrNull()?.asJsonObject
                val posterPath = firstResult?.get("poster_path")?.asString
                posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
            }
            .onErrorResume { e ->
                println("Failed to fetch poster for $title ($year): ${e.message}")
                Mono.just("")
            }
    }
}
