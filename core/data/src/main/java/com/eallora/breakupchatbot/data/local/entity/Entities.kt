package com.eallora.breakupchatbot.data.local.entity

import androidx.room.*
import com.eallora.breakupchatbot.domain.model.AIPersona
import java.util.UUID

/**
 * Room entity for Conversation.
 */
@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val persona: AIPersona,
    val createdAt: Long,
    val updatedAt: Long,
    val title: String? = null,
    val isArchived: Boolean = false
)

/**
 * Room entity for Message.
 */
@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(
        entity = ConversationEntity::class,
        parentColumns = ["id"],
        childColumns = ["conversationId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("conversationId")]
)
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val content: String,
    val role: com.eallora.breakupchatbot.domain.model.MessageRole,
    val timestamp: Long,
    val isFromLocalModel: Boolean = false
)

/**
 * Room entity for Exercise.
 */
@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: com.eallora.breakupchatbot.domain.model.ExerciseType,
    val createdAt: Long,
    val completedAt: Long? = null,
    val persona: AIPersona? = null
)

/**
 * Room entity for Thought Record.
 */
@Entity(
    tableName = "thought_records",
    foreignKeys = [ForeignKey(
        entity = ExerciseEntity::class,
        parentColumns = ["id"],
        childColumns = ["exerciseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("exerciseId")]
)
data class ThoughtRecordEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val situation: String,
    val emotion: String,
    val automaticThought: String,
    val evidenceFor: String,
    val evidenceAgainst: String,
    val alternativeThought: String,
    val outcome: String? = null,
    val timestamp: Long
)

/**
 * Room entity for Scheduled Activity (Behavioral Activation).
 */
@Entity(
    tableName = "activities",
    foreignKeys = [ForeignKey(
        entity = ExerciseEntity::class,
        parentColumns = ["id"],
        childColumns = ["exerciseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("exerciseId")]
)
data class ScheduledActivityEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val activityName: String,
    val scheduledTime: Long,
    val completedAt: Long? = null,
    val notes: String? = null
)

/**
 * Room entity for Progress Entry.
 */
@Entity(tableName = "progress_entries")
data class ProgressEntryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val date: Long,
    val moodScore: Int? = null,
    val exerciseCount: Int,
    val messageCount: Int,
    val streak: Int
)

/**
 * Room entity for User (account sync).
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val createdAt: Long,
    val lastSyncAt: Long? = null,
    val deviceId: String
)