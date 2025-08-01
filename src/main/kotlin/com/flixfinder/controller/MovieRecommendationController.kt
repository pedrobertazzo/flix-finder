package com.flixfinder.controller

import com.flixfinder.model.dto.RecommendationRequest
import com.flixfinder.model.dto.RecommendationResponse
import com.flixfinder.service.MovieRecommendationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/movies")
class MovieRecommendationController(
    private val movieRecommendationService: MovieRecommendationService
) {
    @PostMapping("/recommendations")
    suspend fun getRecommendations(@RequestBody request: RecommendationRequest): ResponseEntity<RecommendationResponse> {
        val recommendations = movieRecommendationService.getMovieRecommendations(
            request.userPreferences,
            request.genres.map { it.name }
        )

        return ResponseEntity.ok().body(RecommendationResponse(recommendations))
    }
}
