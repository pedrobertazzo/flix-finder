package com.flixfinder.model.entity

import com.flixfinder.model.dto.Movie
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "user_backlog")
data class UserBacklog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    val title: String,

    val genre: String? = null,

    val releaseYear: Int? = null,

    val description: String? = null,

    val addedAt: LocalDateTime = LocalDateTime.now()
) {
    constructor(user: User, movie: Movie) : this(
        id = null,
        user = user,
        title = movie.title,
        genre = movie.genre.toString(),
        releaseYear = movie.releaseYear,
        description = movie.description
    )
}
