package com.eallora.breakupchatbot.data.ai

import com.eallora.breakupchatbot.common.Result
import com.eallora.breakupchatbot.data.remote.api.SyncApi
import com.eallora.breakupchatbot.domain.model.AIPersona
import com.eallora.breakupchatbot.domain.model.Message
import javax.inject.Inject

/**
 * LLM adapter interface.
 */
interface LlmAdapter {
    suspend fun sendMessage(messages: List<Message>, persona: AIPersona): Result<String>
}

/**
 * Cloud LLM adapter (OpenRouter/OpenAI compatible).
 */
class CloudLlmAdapter @Inject constructor(
    private val api: SyncApi,
    private val apiKeyProvider: com.eallora.breakupchatbot.data.security.ApiKeyProvider
) : LlmAdapter {
    
    override suspend fun sendMessage(
        messages: List<Message>,
        persona: AIPersona
    ): Result<String> {
        val apiKey = apiKeyProvider.getApiKey() ?: return Result.Error("No API key configured")
        // TODO: Implement actual API call with proper endpoint
        // For now, return placeholder
        return Result.Success("Cloud LLM response placeholder - endpoint not implemented")
    }
}

/**
 * Local LLM adapter (llama.cpp / MediaPipe).
 */
class LocalLlmAdapter @Inject constructor() : LlmAdapter {
    override suspend fun sendMessage(
        messages: List<Message>,
        persona: AIPersona
    ): Result<String> {
        // Placeholder - actual implementation requires native model loading
        return Result.Success("Local model response placeholder - model not loaded")
    }
}

/**
 * LLM Router selects appropriate adapter based on availability.
 */
class LlmRouter @Inject constructor(
    private val cloudAdapter: CloudLlmAdapter,
    private val localAdapter: LocalLlmAdapter,
    private val apiKeyProvider: com.eallora.breakupchatbot.data.security.ApiKeyProvider
) : LlmAdapter {
    
    override suspend fun sendMessage(
        messages: List<Message>,
        persona: AIPersona
    ): Result<String> {
        return if (apiKeyProvider.hasApiKey()) {
            cloudAdapter.sendMessage(messages, persona)
        } else {
            localAdapter.sendMessage(messages, persona)
        }
    }
}