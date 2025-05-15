package com.flixfinder.controller

import com.flixfinder.model.dto.UserBacklogRequest
import com.flixfinder.model.dto.UserBacklogResponse
import com.flixfinder.service.UserBacklogService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/backlog")
class UserBacklogController(
    private val userBacklogService: UserBacklogService
) {
    @GetMapping
    fun getBacklog(
        @RequestParam(required = true) userId: Long
    ): ResponseEntity<UserBacklogResponse> {
        val response = userBacklogService.getBacklogItems(userId)
        return ResponseEntity.ok().body(response)
    }

    @PostMapping("/add")
    fun addBacklogItems(@RequestBody request: UserBacklogRequest): ResponseEntity<Void> {
        userBacklogService.addBacklogItems(request.userId, request.movies)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{userId}/item/{backlogItemId}")
    fun addBacklogItems(@PathVariable userId: Long, @PathVariable backlogItemId: Long): ResponseEntity<Void> {
        userBacklogService.removeBacklogItem(userId, backlogItemId)
        return ResponseEntity.ok().build()
    }
}
