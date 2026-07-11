package com.eallora.breakupchatbot.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Request for user registration.
 */
@Serializable
data class RegisterRequest(
    val email: String,
    val publicKey: String,
    val salt: String
)

/**
 * Request for account login.
 */
@Serializable
data class LoginRequest(
    val email: String
)

/**
 * Response for authentication operations.
 */
@Serializable
data class AuthResponse(
    val token: String,
    val userId: String
)

/**
 * Request for syncing changes.
 */
@Serializable
data class SyncRequest(
    val deviceId: String,
    val changes: List<EncryptedChangeDto>
)

/**
 * Response for sync operations.
 */
@Serializable
data class SyncResponse(
    val changes: List<EncryptedChangeDto>,
    val serverTimestamp: Long
)

/**
 * Encrypted change data transfer object.
 */
@Serializable
data class EncryptedChangeDto(
    val id: String,
    val type: String,
    val encryptedData: String,
    val timestamp: Long
)