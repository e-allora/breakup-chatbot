package com.eallora.breakupchatbot.data.local.dao

import androidx.room.*
import com.eallora.breakupchatbot.data.local.entity.ConversationEntity
import com.eallora.breakupchatbot.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE isArchived = 0 ORDER BY updatedAt DESC")
    fun getAllActive(): Flow<List<ConversationEntity>>

    @Transaction
    @Query("SELECT * FROM conversations WHERE id = :id")
    fun getByIdWithMessages(id: String): Flow<List<ConversationWithMessagesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conversation: ConversationEntity)

    @Query("UPDATE conversations SET updatedAt = :timestamp WHERE id = :id")
    suspend fun updateTimestamp(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE conversations SET isArchived = 1 WHERE id = :id")
    suspend fun archive(id: String)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteById(id: String)
}

/**
 * Room projection for Conversation with Messages.
 */
data class ConversationWithMessagesEntity(
    val conversation: ConversationEntity,
    val messages: List<MessageEntity>
)