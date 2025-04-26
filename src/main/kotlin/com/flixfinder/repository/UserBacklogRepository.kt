package com.flixfinder.repository

import com.flixfinder.model.entity.UserBacklog
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserBacklogRepository : JpaRepository<UserBacklog, Long> {

    // Find all backlog entries for a specific user
    fun findByUserId(userId: Long): List<UserBacklog>

    // Delete a specific movie from a user's backlog
    @Modifying
    @Transactional
    @Query("DELETE FROM UserBacklog ub WHERE ub.title = :movieTitle")
    fun removeMovieFromBacklog(@Param("movieTitle") movieId: String)
}
