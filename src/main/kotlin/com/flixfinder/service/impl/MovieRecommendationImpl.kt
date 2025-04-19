package com.flixfinder.service.impl

import com.flixfinder.service.api.MovieRecommendationService
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatLanguageModel
import org.springframework.stereotype.Service



@Service
class MovieRecommendationImpl(
    private val chatLanguageModel: ChatLanguageModel
) : MovieRecommendationService {

    override fun getMovieRecommendation(preferences: String, genres: List<String>): String {
        val prompt = """
            You are a movie recommendation assistant, suggest 3-5 movies based on the following user query:
            "$preferences"
            FOr the following genres: ${genres.joinToString(", ")}
            
            Present only the name of the movie, the year of release and the genre, no other information is needed.
            
            Separate each movie suggestion with a new line.
        """.trimIndent()

        val messages = listOf(
            UserMessage(prompt)
        )

        return try {
            val response = chatLanguageModel.generate(messages)
            response.content().text()
        } catch (e: Exception) {
            "Sorry, I couldn't generate movie recommendations at the moment. Please try again later."
        }
    }
}