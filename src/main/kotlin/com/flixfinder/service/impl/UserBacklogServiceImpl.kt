package com.flixfinder.service.impl

import com.flixfinder.model.dto.BacklogItem
import com.flixfinder.model.dto.Genre
import com.flixfinder.model.dto.Movie
import com.flixfinder.model.dto.UserBacklogResponse
import com.flixfinder.model.entity.User
import com.flixfinder.model.entity.UserBacklog
import com.flixfinder.repository.UserBacklogRepository
import com.flixfinder.repository.UserRepository
import com.flixfinder.service.UserBacklogService
import org.springframework.stereotype.Service

@Service
class UserBacklogServiceImpl(
    private val userBacklogRepository: UserBacklogRepository,
    private val userRepository: UserRepository
) : UserBacklogService {
    override fun getBacklogItems(userId: Long): UserBacklogResponse {
        if (userId <= 0) throw IllegalArgumentException("Invalid user ID: $userId")

        val backlogItems = userBacklogRepository.findByUserId(userId)

        return UserBacklogResponse(
            userId = userId,
            movies = backlogItems
                .map { item ->
                    BacklogItem(
                        backlogItemId = item.id,
                        movie = Movie(
                            title = item.title,
                            releaseYear = item.releaseYear ?: 0,
                            description = item.description.orEmpty(),
                            genre = parseGenreSafely(item.genre)
                        )
                    )
                }
        )
    }

    private fun parseGenreSafely(genreString: String?): Genre {
        if (genreString == null) return Genre.UNKNOWN
        return try {
            Genre.valueOf(genreString.uppercase())
        } catch (e: IllegalArgumentException) {
            Genre.UNKNOWN
        }
    }

    override fun addBacklogItems(userId: Long, movies: List<Movie>) {
        saveRecommendationsToBacklog(userId, movies)
    }

    override fun removeBacklogItem(userId: Long, backlogItemId: Long) {
        val backlogItems = userBacklogRepository.findByUserId(userId)
        val itemToRemove = backlogItems.find { it.id == backlogItemId }

        if (itemToRemove != null) {
            userBacklogRepository.delete(itemToRemove)
        } else {
            throw IllegalArgumentException("Backlog item with ID $backlogItemId not found for user $userId.")
        }
    }

    private fun saveRecommendationsToBacklog(userId: Long, movies: List<Movie>) {
        val user = userRepository.findById(userId)
        if (user.isEmpty) throw IllegalArgumentException("Invalid user ID: $userId")
        val backlogItems = movies.map { UserBacklog(user.get(), it) }
        userBacklogRepository.saveAll(backlogItems)
    }
}
