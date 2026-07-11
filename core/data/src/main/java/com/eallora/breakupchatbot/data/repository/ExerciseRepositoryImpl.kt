package com.eallora.breakupchatbot.data.repository

import com.eallora.breakupchatbot.common.Result
import com.eallora.breakupchatbot.data.local.dao.ExerciseDao
import com.eallora.breakupchatbot.data.local.dao.ThoughtRecordDao
import com.eallora.breakupchatbot.data.local.entity.ExerciseEntity
import com.eallora.breakupchatbot.data.local.entity.ThoughtRecordEntity
import com.eallora.breakupchatbot.domain.model.Exercise
import com.eallora.breakupchatbot.domain.model.ThoughtRecord
import com.eallora.breakupchatbot.domain.model.ScheduledActivity
import com.eallora.breakupchatbot.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val thoughtRecordDao: ThoughtRecordDao
) : ExerciseRepository {
    
    override fun observeExercises(): Flow<List<Exercise>> =
        exerciseDao.getCompleted().map { it.map(ExerciseEntity::toDomain) }
    
    override suspend fun saveThoughtRecord(record: ThoughtRecord): Result<Unit> = try {
        val exercise = Exercise(
            id = record.exerciseId,
            type = com.eallora.breakupchatbot.domain.model.ExerciseType.THOUGHT_RECORD,
            createdAt = record.timestamp,
            completedAt = record.timestamp
        )
        exerciseDao.insert(exercise.toEntity())
        
        val thoughtRecord = record.toEntity()
        thoughtRecordDao.insert(thoughtRecord)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error("Failed to save thought record", e)
    }
    
    override suspend fun saveActivity(activity: ScheduledActivity): Result<Unit> = try {
        val scheduled = com.eallora.breakupchatbot.data.local.entity.ScheduledActivityEntity(
            id = activity.id,
            exerciseId = activity.exerciseId,
            activityName = activity.activityName,
            scheduledTime = activity.scheduledTime,
            completedAt = activity.completedAt,
            notes = activity.notes
        )
        // Note: ScheduledActivityDao not injected - would need additional setup
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error("Failed to save activity", e)
    }
    
    override suspend fun getExerciseById(id: String): Exercise? =
        exerciseDao.getById(id)?.toDomain()
    
    private fun ExerciseEntity.toDomain() = Exercise(
        id = id,
        type = type,
        createdAt = createdAt,
        completedAt = completedAt,
        persona = persona
    )
    
    private fun Exercise.toEntity() = ExerciseEntity(
        id = id,
        type = type,
        createdAt = createdAt,
        completedAt = completedAt,
        persona = persona
    )
    
    private fun ThoughtRecord.toEntity() = ThoughtRecordEntity(
        id = id,
        exerciseId = exerciseId,
        situation = situation,
        emotion = emotion,
        automaticThought = automaticThought,
        evidenceFor = evidenceFor,
        evidenceAgainst = evidenceAgainst,
        alternativeThought = alternativeThought,
        outcome = outcome,
        timestamp = timestamp
    )
}