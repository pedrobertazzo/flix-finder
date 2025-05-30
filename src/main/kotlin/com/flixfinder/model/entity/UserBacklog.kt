package com.flixfinder.model.entity

import com.flixfinder.model.dto.Movie
import jakarta.persistence.Column
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
data class UserBacklog @JvmOverloads constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @Column(name = "movie_title")
    val title: String = "",

    @Column(name = "movie_genre")
    val genre: String? = null,

    @Column(name = "release_year")
    val releaseYear: Int? = null,

    @Column(name = "movie_description")
    val description: String? = null,

    @Column(name = "added_at")
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
