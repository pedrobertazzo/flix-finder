package com.flixfinder.service

import com.flixfinder.model.Movie
import com.flixfinder.model.dto.UserBacklogResponse

interface UserBacklogService {
    fun getBacklogItems(userId: Long): UserBacklogResponse
    fun addBacklogItems(userId: Long, movies: List<Movie>)
    fun removeBacklogItems(userId: Long, backlogItemIds: List<Long>)
}
