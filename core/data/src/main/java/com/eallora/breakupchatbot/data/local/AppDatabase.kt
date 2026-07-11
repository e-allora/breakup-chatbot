package com.eallora.breakupchatbot.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eallora.breakupchatbot.data.local.dao.ConversationDao
import com.eallora.breakupchatbot.data.local.dao.MessageDao
import com.eallora.breakupchatbot.data.local.dao.ExerciseDao
import com.eallora.breakupchatbot.data.local.dao.ThoughtRecordDao
import com.eallora.breakupchatbot.data.local.dao.ScheduledActivityDao
import com.eallora.breakupchatbot.data.local.dao.ProgressDao
import com.eallora.breakupchatbot.data.local.dao.UserDao
import com.eallora.breakupchatbot.data.local.entity.ConversationEntity
import com.eallora.breakupchatbot.data.local.entity.MessageEntity
import com.eallora.breakupchatbot.data.local.entity.ExerciseEntity
import com.eallora.breakupchatbot.data.local.entity.ThoughtRecordEntity
import com.eallora.breakupchatbot.data.local.entity.ScheduledActivityEntity
import com.eallora.breakupchatbot.data.local.entity.ProgressEntryEntity
import com.eallora.breakupchatbot.data.local.entity.UserEntity

/**
 * Room database for the Breakup Chatbot app.
 */
@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        ExerciseEntity::class,
        ThoughtRecordEntity::class,
        ScheduledActivityEntity::class,
        ProgressEntryEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun thoughtRecordDao(): ThoughtRecordDao
    abstract fun scheduledActivityDao(): ScheduledActivityDao
    abstract fun progressDao(): ProgressDao
    abstract fun userDao(): UserDao
}