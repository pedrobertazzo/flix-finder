package com.flixfinder.service

import com.flixfinder.model.dto.Movie
import com.flixfinder.model.dto.UserBacklogResponse

interface UserBacklogService {
    fun getBacklogItems(userId: Long): UserBacklogResponse
    fun addBacklogItems(userId: Long, movies: List<Movie>)
    fun removeBacklogItem(userId: Long, backlogItemId: Long)
}
