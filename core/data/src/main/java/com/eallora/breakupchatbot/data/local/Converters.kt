package com.eallora.breakupchatbot.data.local

import androidx.room.TypeConverter
import com.eallora.breakupchatbot.domain.model.AIPersona
import com.eallora.breakupchatbot.domain.model.MessageRole
import com.eallora.breakupchatbot.domain.model.ExerciseType

/**
 * Room TypeConverters for enum types.
 */
class Converters {
    @TypeConverter
    fun fromAIPersona(value: AIPersona): String = value.name

    @TypeConverter
    fun toAIPersona(value: String): AIPersona = AIPersona.valueOf(value)

    @TypeConverter
    fun fromMessageRole(value: MessageRole): String = value.name

    @TypeConverter
    fun toMessageRole(value: String): MessageRole = MessageRole.valueOf(value)

    @TypeConverter
    fun fromExerciseType(value: ExerciseType): String = value.name

    @TypeConverter
    fun toExerciseType(value: String): ExerciseType = ExerciseType.valueOf(value)
}