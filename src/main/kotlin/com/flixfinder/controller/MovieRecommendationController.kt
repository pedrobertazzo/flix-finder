package com.flixfinder.controller

import com.flixfinder.model.RecommendationRequest
import com.flixfinder.model.RecommendationResponse
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
    @PostMapping("/recommend")
    fun getRecommendations(@RequestBody request: RecommendationRequest): ResponseEntity<RecommendationResponse> {
        val recommendations = movieRecommendationService.getMovieRecommendation(request.userPreferences, request.genres.map { it.name })

        return ResponseEntity.ok().body(RecommendationResponse(recommendations))
    }
}
