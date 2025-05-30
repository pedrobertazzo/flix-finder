package com.flixfinder.service

import com.flixfinder.model.dto.User

interface UserService {
    suspend fun createUser(user: User): User
    suspend fun deleteUser(id: Long)
}
