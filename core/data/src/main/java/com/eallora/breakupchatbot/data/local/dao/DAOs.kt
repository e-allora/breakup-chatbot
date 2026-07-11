package com.eallora.breakupchatbot.data.local.dao

import androidx.room.*
import com.eallora.breakupchatbot.data.local.entity.ConversationEntity
import com.eallora.breakupchatbot.data.local.entity.MessageEntity
import com.eallora.breakupchatbot.data.local.entity.ExerciseEntity
import com.eallora.breakupchatbot.data.local.entity.ThoughtRecordEntity
import com.eallora.breakupchatbot.data.local.entity.ScheduledActivityEntity
import com.eallora.breakupchatbot.data.local.entity.ProgressEntryEntity
import com.eallora.breakupchatbot.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Conversation entity.
 */
@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE isArchived = 0 ORDER BY updatedAt DESC")
    fun getAllActive(): Flow<List<ConversationEntity>>

    @Transaction
    @Query("SELECT * FROM conversations WHERE id = :id")
    fun getWithMessages(id: String): Flow<Pair<ConversationEntity, List<MessageEntity>>?>

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
 * DAO for Message entity.
 */
@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getByConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId")
    suspend fun getMessageCount(conversationId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<MessageEntity>)

    @Delete
    suspend fun deleteMessage(message: MessageEntity)
}

/**
 * DAO for Exercise entity.
 */
@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE completedAt IS NOT NULL ORDER BY completedAt DESC")
    fun getCompleted(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getById(id: String): ExerciseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: ExerciseEntity)

    @Update
    suspend fun update(exercise: ExerciseEntity)
}

/**
 * DAO for Thought Record entity.
 */
@Dao
interface ThoughtRecordDao {
    @Query("SELECT * FROM thought_records WHERE exerciseId = :exerciseId")
    suspend fun getByExerciseId(exerciseId: String): ThoughtRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: ThoughtRecordEntity)

    @Update
    suspend fun update(record: ThoughtRecordEntity)
}

/**
 * DAO for Scheduled Activity entity.
 */
@Dao
interface ScheduledActivityDao {
    @Query("SELECT * FROM activities WHERE exerciseId = :exerciseId")
    suspend fun getByExerciseId(exerciseId: String): List<ScheduledActivityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: ScheduledActivityEntity)

    @Update
    suspend fun update(activity: ScheduledActivityEntity)
}

/**
 * DAO for Progress Entry entity.
 */
@Dao
interface ProgressDao {
    @Query("SELECT * FROM progress_entries WHERE date >= :fromDate ORDER BY date ASC")
    fun getHistory(fromDate: Long): Flow<List<ProgressEntryEntity>>

    @Query("SELECT * FROM progress_entries ORDER BY date DESC LIMIT 1")
    suspend fun getLatest(): ProgressEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: ProgressEntryEntity)
}

/**
 * DAO for User entity.
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrent(): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)
}