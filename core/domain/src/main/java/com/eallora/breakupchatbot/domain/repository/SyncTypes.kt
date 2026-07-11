package com.eallora.breakupchatbot.domain.repository

/**
 * Sync result for account sync operations.
 */
data class SyncResult(
    val success: Boolean,
    val messageCount: Int,
    val exerciseCount: Int,
    val conflictCount: Int
)

/**
 * Encrypted change for E2E sync.
 */
data class EncryptedChange(
    val id: String,
    val type: ChangeType,
    val encryptedData: String,
    val timestamp: Long
)

/**
 * Type of change for sync.
 */
enum class ChangeType {
    CONVERSATION,
    MESSAGE,
    EXERCISE,
    THOUGHT_RECORD,
    ACTIVITY,
    PROGRESS
}

/**
 * User data class for repository (distinct from domain User to avoid conflict).
 */
data class UserData(
    val id: String,
    val email: String,
    val createdAt: Long,
    val lastSyncAt: Long?,
    val deviceId: String
)