package com.eallora.breakupchatbot.data.repository

import com.eallora.breakupchatbot.common.Result
import com.eallora.breakupchatbot.data.remote.api.SyncApi
import com.eallora.breakupchatbot.data.remote.dto.RegisterRequest
import com.eallora.breakupchatbot.domain.repository.SyncRepository
import com.eallora.breakupchatbot.domain.repository.User
import com.eallora.breakupchatbot.domain.repository.SyncResult
import com.eallora.breakupchatbot.domain.repository.EncryptedChange
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val api: SyncApi
) : SyncRepository {
    
    override suspend fun register(email: String, publicKey: String, salt: String): Result<User> {
        return try {
            val request = RegisterRequest(email = email, publicKey = publicKey, salt = salt)
            val response = api.register(request)
            Result.Success(
                User(
                    id = response.userId,
                    email = email,
                    createdAt = System.currentTimeMillis(),
                    deviceId = response.deviceId
                )
            )
        } catch (e: Exception) {
            Result.Error("Registration failed: ${e.message}", e)
        }
    }
    
    override suspend fun login(email: String): Result<User> {
        return try {
            val response = api.login(email)
            Result.Success(
                User(
                    id = response.userId,
                    email = email,
                    createdAt = System.currentTimeMillis(),
                    deviceId = response.deviceId
                )
            )
        } catch (e: Exception) {
            Result.Error("Login failed: ${e.message}", e)
        }
    }
    
    override suspend fun syncPendingChanges(): Result<SyncResult> {
        return try {
            // Placeholder - needs authentication token
            Result.Success(SyncResult(changesApplied = 0, errors = emptyList()))
        } catch (e: Exception) {
            Result.Error("Sync failed", e)
        }
    }
    
    override suspend fun decryptAndApplyServerChanges(changes: List<EncryptedChange>) {
        // Placeholder for E2E decryption + DB merge
    }
}