package com.flixfinder.service.impl

import com.flixfinder.repository.UserRepository
import com.flixfinder.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.flixfinder.model.dto.User as UserDto
import com.flixfinder.model.entity.User as UserEntity

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    @Transactional
    override suspend fun createUser(user: UserDto): UserDto {
        val userEntity = UserEntity(
            username = user.username,
            email = user.email
        )
        val savedUser = userRepository.save(userEntity)
        return UserDto(
            id = savedUser.id,
            username = savedUser.username,
            email = savedUser.email
        )
    }

    @Transactional
    override suspend fun deleteUser(id: Long) {
        userRepository.deleteById(id)
    }
}
