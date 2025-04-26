package com.flixfinder.repository

import com.flixfinder.model.entity.User
import org.springframework.data.repository.query.Param

interface UserRepository {

    fun findById(@Param("userId") userId: Long): User
}
