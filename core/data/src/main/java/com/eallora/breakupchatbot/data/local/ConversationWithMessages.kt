package com.eallora.breakupchatbot.data.local

import androidx.room.DatabaseView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Convenience data class for conversation with messages.
 */
data class ConversationWithMessages(
    val conversation: Conversation,
    val messages: List<Message>
) {
    data class Conversation(
        val id: String,
        val persona: AIPersona,
        val createdAt: Long,
        val updatedAt: Long,
        val title: String?,
        val isArchived: Boolean
    )
    
    data class Message(
        val id: String,
        val conversationId: String,
        val content: String,
        val role: MessageRole,
        val timestamp: Long,
        val isFromLocalModel: Boolean
    )
    
    enum class AIPersona { THERAPIST, FRIEND, COACH }
    enum class MessageRole { USER, ASSISTANT, SYSTEM }
}