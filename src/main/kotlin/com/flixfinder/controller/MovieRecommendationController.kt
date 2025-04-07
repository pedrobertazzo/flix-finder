package com.flixfinder.controller

import com.flixfinder.model.RecommendationRequest
import com.flixfinder.model.RecommendationResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/movies")
class MovieRecommendationController(
) {
    @PostMapping("/recommend")
    fun getRecommendations(@RequestBody request: RecommendationRequest): RecommendationResponse {
        // Use LLM to generate personalized recommendations
        val recommendations = "Watch everything!!!"

        return RecommendationResponse(recommendations)
    }
}