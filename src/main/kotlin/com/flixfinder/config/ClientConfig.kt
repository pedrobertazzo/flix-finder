package com.flixfinder.config

import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.openai.OpenAiChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ClientConfig {

    @Value("\${openai.apiKey}")
    private lateinit var openAiApiKey: String

    @Value("\${openai.baseUrl}")
    private lateinit var openAiBaseUrl: String

    @Value("\${openai.model}")
    private lateinit var openAiModel: String

    @Value("\${tmdb.apiKey}")
    private lateinit var tmdbApiKey: String

    @Value("\${tmdb.baseUrl}")
    private lateinit var tmdbBaseUrl: String

    @Bean
    fun webClient(): WebClient.Builder {
        return WebClient.builder()
    }

    @Bean
    fun chatLanguageModel(): ChatLanguageModel {
        return OpenAiChatModel.builder()
            .baseUrl(openAiBaseUrl)
            .apiKey(openAiApiKey)
            .modelName(openAiModel)
            .temperature(1.0)
            .build()
    }

    @Bean
    fun tmdbWebClient(webClientBuilder: WebClient.Builder): WebClient {
        return webClientBuilder
            .baseUrl(tmdbBaseUrl)
            .defaultHeader("Authorization", "Bearer $tmdbApiKey")
            .build()
    }
}
