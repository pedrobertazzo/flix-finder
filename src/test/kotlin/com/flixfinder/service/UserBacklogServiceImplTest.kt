package com.flixfinder.service.impl

import com.flixfinder.model.dto.BacklogItem
import com.flixfinder.model.dto.Genre
import com.flixfinder.model.dto.Movie
import com.flixfinder.model.dto.UserBacklogResponse
import com.flixfinder.model.entity.User
import com.flixfinder.model.entity.UserBacklog
import com.flixfinder.repository.UserBacklogRepository
import com.flixfinder.repository.UserRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class UserBacklogServiceImplTest {

    private lateinit var userBacklogRepository: UserBacklogRepository
    private lateinit var userRepository: UserRepository
    private lateinit var userBacklogService: UserBacklogServiceImpl

    @BeforeEach
    fun setup() {
        userBacklogRepository = mockk()
        userRepository = mockk()
        userBacklogService = UserBacklogServiceImpl(userBacklogRepository, userRepository)
    }

    @Test
    fun `getBacklogItems returns user backlog response`() {
        // Given
        val userId = 1L
        val backlogItems = listOf(
            UserBacklog(
                id = 1L,
                user = User(id = userId, username = "user1", email = "user1@example.com"),
                title = "Movie 1",
                description = "Description 1",
                releaseYear = 2023,
                genre = "ACTION"
            ),
            UserBacklog(
                id = 2L,
                user = User(id = userId, username = "user1", email = "user1@example.com"),
                title = "Movie 2",
                description = "Description 2",
                releaseYear = 2025,
                genre = "SCIENCE_FICTION"
            )
        )

        val expectedResponse = UserBacklogResponse(
            userId = userId,
            movies = backlogItems.map { item ->
                BacklogItem(
                    backlogItemId = item.id,
                    movie = Movie(
                        title = item.title,
                        releaseYear = item.releaseYear ?: 0,
                        description = item.description.orEmpty(),
                        genre = Genre.valueOf(item.genre ?: "UNKNOWN")
                    )
                )
            }
        )

        every { userBacklogRepository.findByUserId(userId) } returns backlogItems

        // When
        val result = userBacklogService.getBacklogItems(userId)

        // Then
        assertEquals(expectedResponse.userId, result.userId)
        assertEquals(expectedResponse.movies.size, result.movies.size)
        verify(exactly = 1) { userBacklogRepository.findByUserId(userId) }
    }

    @Test
    fun `getBacklogItems throws exception for invalid userId`() {
        // Given
        val userId = -1L

        // When/Then
        val exception = assertThrows<IllegalArgumentException> {
            userBacklogService.getBacklogItems(userId)
        }

        assertEquals("Invalid user ID: $userId", exception.message)
        verify(exactly = 0) { userBacklogRepository.findByUserId(any()) }
    }

    @Test
    fun `addBacklogItems saves movies to backlog`() {
        // Given
        val userId = 1L
        val movies = listOf(
            Movie(
                title = "Inception",
                releaseYear = 2010,
                description = "A thief who steals corporate secrets through dream-sharing technology",
                genre = Genre.SCIENCE_FICTION
            )
        )
        val user = User(id = userId, username = "user1", email = "user1@example.com")
        val backlogItems = movies.map {
            UserBacklog(
                user = user,
                Movie(
                    title = it.title,
                    description = it.description,
                    releaseYear = it.releaseYear,
                    genre = it.genre
                )
            )
        }

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { userBacklogRepository.saveAll(any<List<UserBacklog>>()) } returns backlogItems

        // When
        userBacklogService.addBacklogItems(userId, movies)

        // Then
        verify(exactly = 1) { userRepository.findById(userId) }
        verify(exactly = 1) { userBacklogRepository.saveAll(any<List<UserBacklog>>()) }
    }

    @Test
    fun `addBacklogItems throws exception when user does not exist`() {
        // Given
        val userId = 1L
        val movies = listOf(
            Movie(
                title = "Inception",
                releaseYear = 2010,
                description = "A thief who steals corporate secrets through dream-sharing technology",
                genre = Genre.SCIENCE_FICTION
            )
        )

        every { userRepository.findById(userId) } returns Optional.empty()

        // When/Then
        val exception = assertThrows<IllegalArgumentException> {
            userBacklogService.addBacklogItems(userId, movies)
        }

        assertEquals("Invalid user ID: $userId", exception.message)
        verify(exactly = 1) { userRepository.findById(userId) }
        verify(exactly = 0) { userBacklogRepository.saveAll(any<List<UserBacklog>>()) }
    }

    @Test
    fun `removeBacklogItems removes items by ids`() {
        // Given
        val userId = 1L
        val user = User(id = userId, username = "user1", email = "user1@example.com")
        val backlogItem = UserBacklog(
            id = 10L,
            user = user,
            title = "Movie title",
            description = "Description",
            releaseYear = 2020,
            genre = "ACTION"
        )

        every { userBacklogRepository.findByUserId(userId) } returns listOf(backlogItem)
        every { userBacklogRepository.delete(backlogItem) } just runs

        // When
        userBacklogService.removeBacklogItem(userId, 10L)

        // Then
        verify(exactly = 1) { userBacklogRepository.findByUserId(userId) }
        verify(exactly = 1) { userBacklogRepository.delete(backlogItem) }
    }
}
