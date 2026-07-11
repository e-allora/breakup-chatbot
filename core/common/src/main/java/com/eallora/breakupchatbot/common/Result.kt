package com.eallora.breakupchatbot.common

import kotlinx.serialization.Serializable

/**
 * Sealed wrapper for network and database operation results.
 */
@Serializable
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val message: String, val throwable: Throwable? = null) : Result<Nothing>
    data object Loading : Result<Nothing>
}