package com.eallora.breakupchatbot.data.remote.api

import com.eallora.breakupchatbot.data.remote.dto.AuthResponse
import com.eallora.breakupchatbot.data.remote.dto.RegisterRequest
import com.eallora.breakupchatbot.data.remote.dto.SyncRequest
import com.eallora.breakupchatbot.data.remote.dto.SyncResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit API for account sync (E2E encrypted).
 */
interface SyncApi {
    @GET("sync/chunks")
    suspend fun getChanges(
        @Header("Authorization") token: String,
        @Query("since") timestamp: Long
    ): SyncResponse

    @POST("sync/chunks")
    suspend fun uploadChanges(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): SyncResponse

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthResponse

    @POST("auth/login")
    suspend fun login(
        @Header("Authorization") email: String
    ): AuthResponse
}