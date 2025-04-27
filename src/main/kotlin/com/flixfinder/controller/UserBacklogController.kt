package com.flixfinder.controller

import com.flixfinder.model.dto.UserBacklogResponse
import com.flixfinder.service.UserBacklogService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/userBacklog")
class UserBacklogController(
    private val userBacklogService: UserBacklogService
) {
    @GetMapping("/backlogItems")
    fun getBacklog(
        @RequestParam(required = true) userId: Long
    ): ResponseEntity<UserBacklogResponse> {
        val response = userBacklogService.getBacklogItems(userId)
        return ResponseEntity.ok().body(response)
    }
}
