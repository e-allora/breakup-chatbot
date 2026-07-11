package com.eallora.breakupchatbot.domain.repository

import com.eallora.breakupchatbot.common.Result
import com.eallora.breakupchatbot.domain.model.Conversation
import com.eallora.breakupchatbot.domain.model.ConversationWithMessages
import com.eallora.breakupchatbot.domain.model.ProgressEntry
import com.eallora.breakupchatbot.domain.model.ScheduledActivity
import com.eallora.breakupchatbot.domain.model.AIPersona
import com.eallora.breakupchatbot.domain.model.Exercise
import com.eallora.breakupchatbot.domain.model.ThoughtRecord
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for conversation management.
 */
interface ConversationRepository {
    fun observeConversation(id: String): Flow<ConversationWithMessages?>
    fun observeActiveConversations(): Flow<List<Conversation>>
    suspend fun createConversation(persona: AIPersona): Result<Conversation>
    suspend fun sendMessage(message: String, conversationId: String): Result<String>
    suspend fun archiveConversation(id: String)
    suspend fun deleteConversation(id: String)
}

/**
 * Repository interface for exercise management.
 */
interface ExerciseRepository {
    fun observeExercises(): Flow<List<Exercise>>
    suspend fun saveThoughtRecord(record: ThoughtRecord): Result<Unit>
    suspend fun saveActivity(activity: ScheduledActivity): Result<Unit>
    suspend fun getExerciseById(id: String): Exercise?
}

/**
 * Repository interface for progress tracking.
 */
interface ProgressRepository {
    fun observeProgressHistory(): Flow<List<ProgressEntry>>
    fun observeCurrentStreak(): Flow<Int>
    suspend fun recordProgress(moodScore: Int?, exerciseCount: Int, messageCount: Int)
}

/**
 * Repository interface for account sync.
 */
interface SyncRepository {
    suspend fun register(email: String, publicKey: String, salt: String): Result<User>
    suspend fun login(email: String): Result<User>
    suspend fun syncPendingChanges(): Result<SyncResult>
    suspend fun decryptAndApplyServerChanges(changes: List<EncryptedChange>)
}