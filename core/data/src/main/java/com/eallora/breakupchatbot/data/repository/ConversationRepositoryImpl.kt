package com.eallora.breakupchatbot.data.repository

import com.eallora.breakupchatbot.common.Result
import com.eallora.breakupchatbot.data.local.dao.ConversationDao
import com.eallora.breakupchatbot.data.local.dao.MessageDao
import com.eallora.breakupchatbot.data.local.dao.ProgressDao
import com.eallora.breakupchatbot.data.local.entity.ConversationEntity
import com.eallora.breakupchatbot.data.local.entity.MessageEntity
import com.eallora.breakupchatbot.data.local.entity.ProgressEntryEntity
import com.eallora.breakupchatbot.domain.model.Conversation
import com.eallora.breakupchatbot.domain.model.ConversationWithMessages
import com.eallora.breakupchatbot.domain.model.Message
import com.eallora.breakupchatbot.domain.model.AIPersona
import com.eallora.breakupchatbot.domain.repository.ConversationRepository
import com.eallora.breakupchatbot.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val progressRepository: ProgressRepository
) : ConversationRepository {
    
    override fun observeConversation(id: String): Flow<ConversationWithMessages?> =
        conversationDao.getByIdWithMessages(id).map { entities ->
            entities.firstOrNull()?.let { entity ->
                ConversationWithMessages(
                    conversation = entity.conversation.toDomain(),
                    messages = entity.messages.map { it.toDomain() }
                )
            }
        }
    
    override fun observeActiveConversations(): Flow<List<Conversation>> =
        conversationDao.getAllActive().map { it.map(ConversationEntity::toDomain) }
    
    override suspend fun createConversation(persona: AIPersona): Result<Conversation> {
        return try {
            val conversation = Conversation(persona = persona)
            conversationDao.insert(conversation.toEntity())
            Result.Success(conversation)
        } catch (e: Exception) {
            Result.Error("Failed to create conversation", e)
        }
    }
    
    override suspend fun sendMessage(message: String, conversationId: String): Result<String> {
        return try {
            // Send user message
            val userMsg = Message(
                conversationId = conversationId,
                content = message,
                role = com.eallora.breakupchatbot.domain.model.MessageRole.USER
            )
            messageDao.insert(userMsg.toEntity())
            
            // Update conversation timestamp
            conversationDao.updateTimestamp(conversationId)
            
            // Record progress
            progressRepository.recordProgress(moodScore = null, exerciseCount = 0, messageCount = 1)
            
            Result.Success(userMsg.id)
        } catch (e: Exception) {
            Result.Error("Failed to send message", e)
        }
    }
    
    override suspend fun archiveConversation(id: String) {
        conversationDao.archive(id)
    }
    
    override suspend fun deleteConversation(id: String) {
        // Messages cascade deleted via FK
        conversationDao.deleteById(id)
    }
    
    private fun ConversationEntity.toDomain() = Conversation(
        id = id,
        persona = persona,
        createdAt = createdAt,
        updatedAt = updatedAt,
        title = title,
        isArchived = isArchived
    )
    
    private fun MessageEntity.toDomain() = Message(
        id = id,
        conversationId = conversationId,
        content = content,
        role = role,
        timestamp = timestamp,
        isFromLocalModel = isFromLocalModel
    )
    
    private fun Conversation.toEntity() = ConversationEntity(
        id = id,
        persona = persona,
        createdAt = createdAt,
        updatedAt = updatedAt,
        title = title,
        isArchived = isArchived
    )
    
    private fun Message.toEntity() = MessageEntity(
        id = id,
        conversationId = conversationId,
        content = content,
        role = role,
        timestamp = timestamp,
        isFromLocalModel = isFromLocalModel
    )
}