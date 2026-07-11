package com.eallora.breakupchatbot.domain.model

import java.util.UUID

/**
 * AI Persona types for the chatbot.
 */
enum class AIPersona {
    THERAPIST,
    FRIEND,
    COACH;

    fun getSystemPrompt(): String = when (this) {
        THERAPIST -> """
            You are a compassionate therapist specializing in breakup recovery.
            Use Cognitive Behavioral Therapy techniques. Ask open-ended questions.
            Validate emotions. Help identify cognitive distortions.
            Keep responses under 200 words. Never diagnose.
        """.trimIndent()

        FRIEND -> """
            You are a supportive friend who cares deeply.
            Be empathetic and understanding. Share relatable experiences.
            Use casual, warm language. Avoid clinical terms.
            Encourage but don't push. Keep responses conversational.
        """.trimIndent()

        COACH -> """
            You are a motivational coach helping rebuild confidence.
            Focus on actionable steps and positive reframing.
            Use energetic, encouraging language. Set small goals.
            Celebrate progress. Be direct and practical.
        """.trimIndent()
    }

    fun getTitle(): String = when (this) {
        THERAPIST -> "Therapist"
        FRIEND -> "Friend"
        COACH -> "Coach"
    }

    fun getDescription(): String = when (this) {
        THERAPIST -> "Clinical approach using CBT techniques, reflective questions, and emotional validation."
        FRIEND -> "Conversational and supportive, sharing relatable experiences in a casual tone."
        COACH -> "Action-oriented guidance focused on goals, motivation, and practical steps."
    }
}

/**
 * Message role in a conversation.
 */
enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

/**
 * Exercise types available in the app.
 */
enum class ExerciseType {
    THOUGHT_RECORD,
    BEHAVIORAL_ACTIVATION
}

/**
 * Core entity: Conversation between user and AI.
 */
data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val persona: AIPersona,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val title: String? = null,
    val isArchived: Boolean = false
)

/**
 * Core entity: Message in a conversation.
 */
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val content: String,
    val role: MessageRole,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromLocalModel: Boolean = false
)

/**
 * Core entity: CBT Exercise record.
 */
data class Exercise(
    val id: String = UUID.randomUUID().toString(),
    val type: ExerciseType,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val persona: AIPersona? = null
)

/**
 * Core entity: Thought Record for CBT exercises.
 */
data class ThoughtRecord(
    val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val situation: String,
    val emotion: String,
    val automaticThought: String,
    val evidenceFor: String,
    val evidenceAgainst: String,
    val alternativeThought: String,
    val outcome: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Core entity: Scheduled activity for Behavioral Activation.
 */
data class ScheduledActivity(
    val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val activityName: String,
    val scheduledTime: Long,
    val completedAt: Long? = null,
    val notes: String? = null
)

/**
 * Core entity: Progress tracking entry for analytics.
 */
data class ProgressEntry(
    val id: String = UUID.randomUUID().toString(),
    val date: Long,
    val moodScore: Int? = null,
    val exerciseCount: Int,
    val messageCount: Int,
    val streak: Int
)

/**
 * Core entity: User account for sync.
 */
data class User(
    val id: String,
    val email: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSyncAt: Long? = null,
    val deviceId: String
)

/**
 * Combined data class for conversation with messages.
 */
data class ConversationWithMessages(
    val conversation: Conversation,
    val messages: List<Message>
)