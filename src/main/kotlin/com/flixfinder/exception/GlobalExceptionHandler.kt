package com.flixfinder.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.flixfinder.model.dto.Genre
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(InvalidFormatException::class)
    fun handleInvalidFormatException(ex: InvalidFormatException): ResponseEntity<Map<String, Any>> {
        if (ex.targetType == Genre::class.java) {
            val response = mapOf(
                "error" to "Invalid genre value",
                "message" to "Allowed genres are: ${Genre.entries.joinToString(", ")}",
                "allowedGenres" to Genre.entries.map { it.name }
            )
            return ResponseEntity(response, HttpStatus.BAD_REQUEST)
        }

        val response = mapOf(
            "error" to "Invalid format",
            "message" to ex.message.orEmpty(),
            "details" to "An invalid value was provided for a field of type: ${ex.targetType.simpleName}"
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }
}
