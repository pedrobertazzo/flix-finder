package com.flixfinder.service

import com.flixfinder.model.Genre
import com.flixfinder.model.Movie
import com.flixfinder.service.impl.MovieRecommendationImpl
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.output.Response
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MovieRecommendationImplTest {

    private lateinit var chatLanguageModel: ChatLanguageModel
    private lateinit var movieRecommendationImpl: MovieRecommendationImpl

    @BeforeEach
    fun setup() {
        chatLanguageModel = mockk()
        movieRecommendationImpl = MovieRecommendationImpl(chatLanguageModel)
    }

    @Test
    fun `getMovieRecommendations returns list of movies when API returns valid JSON`() {
        // Given
        val jsonResponse = """
            [
              {
                "title": "The Shawshank Redemption",
                "releaseYear": 1994,
                "description": "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                "genre": "DRAMA"
              },
              {
                "title": "The Dark Knight",
                "releaseYear": 2008,
                "description": "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
                "genre": "ACTION"
              }
            ]
        """.trimIndent()

        // Capture the prompt to verify it contains expected data
        val promptCaptor = slot<List<ChatMessage>>()

        every {
            chatLanguageModel.generate(capture(promptCaptor))
        } returns Response(AiMessage("```json\n$jsonResponse\n```"))

        // When
        val preferences = "I like thoughtful movies with good plots"
        val genres = listOf("DRAMA", "ACTION")
        val userId = 1L
        val recommendations = movieRecommendationImpl.getMovieRecommendations(preferences, genres, userId)

        // Then
        val expectedMovies = listOf(
            Movie(
                title = "The Shawshank Redemption",
                releaseYear = 1994,
                description = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                genre = Genre.DRAMA
            ),
            Movie(
                title = "The Dark Knight",
                releaseYear = 2008,
                description = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
                genre = Genre.ACTION
            )
        )

        assertEquals(expectedMovies, recommendations)

        // Verify the prompt contains user preferences and genres
        val promptText = (promptCaptor.captured.first() as UserMessage).text()
        assertTrue(promptText.contains(preferences))
        genres.forEach { genre ->
            assertTrue(promptText.contains(genre))
        }
    }

    @Test
    fun `getMovieRecommendations handles empty list when API returns empty array`() {
        // Given
        every {
            chatLanguageModel.generate(any<List<ChatMessage>>())
        } returns Response(AiMessage("```json\n[]\n```"))

        // When
        val recommendations = movieRecommendationImpl.getMovieRecommendations(
            "Some very specific preferences that won't match anything",
            listOf("DOCUMENTARY"),
            1L
        )

        // Then
        assertTrue(recommendations.isEmpty())
    }

    @Test
    fun `getMovieRecommendations handles JSON without Markdown code blocks`() {
        // Given
        val jsonResponse = """
            [{"title":"Inception","releaseYear":2010,"description":"A thief who steals corporate secrets through the use of dream-sharing technology.","genre":"SCI_FI"}]
        """.trimIndent()

        every {
            chatLanguageModel.generate(any<List<ChatMessage>>())
        } returns Response(AiMessage(jsonResponse))

        // When
        val recommendations = movieRecommendationImpl.getMovieRecommendations(
            "Dreams and mind-bending reality",
            listOf("SCI_FI"),
            1L
        )

        // Then
        assertEquals(1, recommendations.size)
        assertEquals("Inception", recommendations[0].title)
        assertEquals(Genre.SCI_FI, recommendations[0].genre)
    }

    @Test
    fun `getMovieRecommendations returns empty list when API returns invalid JSON`() {
        // Given
        every {
            chatLanguageModel.generate(any<List<ChatMessage>>())
        } returns Response(AiMessage("This is not a JSON response"))

        // When
        val recommendations = movieRecommendationImpl.getMovieRecommendations(
            "Any preferences",
            listOf("COMEDY"),
            1L
        )

        // Then
        assertTrue(recommendations.isEmpty())
    }

    @Test
    fun `getMovieRecommendations throws RuntimeException when API call fails`() {
        // Given
        every {
            chatLanguageModel.generate(any<List<ChatMessage>>())
        } throws RuntimeException("API service unavailable")

        // When/Then
        val exception = assertThrows(RuntimeException::class.java) {
            movieRecommendationImpl.getMovieRecommendations(
                "Any preferences",
                listOf("COMEDY"),
                1L
            )
        }

        assertTrue(exception.message!!.contains("Failed to generate movie recommendations"))
    }
}
