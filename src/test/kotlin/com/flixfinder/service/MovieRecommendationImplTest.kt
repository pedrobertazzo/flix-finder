package com.flixfinder.service

import com.flixfinder.model.Genre
import com.flixfinder.service.impl.MovieRecommendationImpl
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.output.Response
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import java.net.URI
import java.util.function.Function

class MovieRecommendationImplTest {

    private lateinit var chatLanguageModel: ChatLanguageModel
    private lateinit var webClient: WebClient
    private lateinit var webClientBuilder: WebClient.RequestHeadersUriSpec<*>
    private lateinit var webClientUri: WebClient.RequestHeadersSpec<*>
    private lateinit var movieRecommendationImpl: MovieRecommendationImpl

    @BeforeEach
    fun setup() {
        chatLanguageModel = mockk()

        // Mock the WebClient chain
        webClientUri = mockk()
        webClientBuilder = mockk()
        webClient = mockk()
        val responseSpec = mockk<WebClient.ResponseSpec>()

        every { webClient.get() } returns webClientBuilder
        every { webClientBuilder.uri(any<Function<UriBuilder, URI>>()) } returns webClientUri
        every { webClientUri.retrieve() } returns responseSpec

        // Add this line to mock the onStatus method
        every { responseSpec.onStatus(any(), any()) } returns responseSpec

        // Keep this line to mock bodyToMono
        every { responseSpec.bodyToMono(String::class.java) } returns Mono.empty()

        movieRecommendationImpl = MovieRecommendationImpl(chatLanguageModel, webClient)
    }

    @Test
    fun `getMovieRecommendations returns list of movies when API returns valid JSON`() = runBlocking {
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

        // Mock the model response without capturing the prompt
        every {
            chatLanguageModel.generate(any<List<ChatMessage>>())
        } returns Response(AiMessage("```json\n$jsonResponse\n```"))

        // When
        val preferences = "I like thoughtful movies with good plots"
        val genres = listOf("DRAMA", "ACTION")
        val userId = 1L
        val recommendations = movieRecommendationImpl.getMovieRecommendations(preferences, genres, userId)

        // Then
        assertEquals(2, recommendations.size)

        // Verify first movie
        assertEquals("The Shawshank Redemption", recommendations[0].title)
        assertEquals(1994, recommendations[0].releaseYear)
        assertEquals("Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.", recommendations[0].description)
        assertEquals(Genre.DRAMA, recommendations[0].genre)

        // Verify second movie
        assertEquals("The Dark Knight", recommendations[1].title)
        assertEquals(2008, recommendations[1].releaseYear)
        assertEquals("When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.", recommendations[1].description)
        assertEquals(Genre.ACTION, recommendations[1].genre)
    }

    @Test
    fun `getMovieRecommendations handles empty list when API returns empty array`() = runBlocking {
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
    fun `getMovieRecommendations handles JSON without Markdown code blocks`() = runBlocking {
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
    fun `getMovieRecommendations returns empty list when API returns invalid JSON`() = runBlocking {
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
    fun `getMovieRecommendations throws RuntimeException when API call fails`() = runBlocking {
        // Given
        every {
            chatLanguageModel.generate(any<List<ChatMessage>>())
        } throws RuntimeException("API service unavailable")

        // When/Then
        val exception = assertThrows(RuntimeException::class.java) {
            runBlocking {
                movieRecommendationImpl.getMovieRecommendations(
                    "Any preferences",
                    listOf("COMEDY"),
                    1L
                )
            }
        }

        assertTrue(exception.message!!.contains("Failed to generate movie recommendations"))
    }

    @Test
    fun `getMovieRecommendations includes poster URLs when available`() = runBlocking {
        // Given
        val jsonResponse = """
        [{"title":"Inception","releaseYear":2010,"description":"A thief who steals corporate secrets through the use of dream-sharing technology.","genre":"SCI_FI"}]
        """.trimIndent()

        val tmdbResponse = """
        {
          "results": [
            {
              "poster_path": "/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg",
              "title": "Inception"
            }
          ]
        }
        """.trimIndent()

        every {
            chatLanguageModel.generate(any<List<ChatMessage>>())
        } returns Response(AiMessage(jsonResponse))

        // Mock the WebClient response for poster URL
        val retrieveMock = mockk<WebClient.ResponseSpec>()
        val monoMock = Mono.just(tmdbResponse)

        every { webClientUri.retrieve() } returns retrieveMock
        // Add onStatus mocking
        every { retrieveMock.onStatus(any(), any()) } returns retrieveMock
        every { retrieveMock.bodyToMono(String::class.java) } returns monoMock

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
        assertEquals("https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg", recommendations[0].imageUrl)
    }
}
