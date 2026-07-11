package com.eallora.breakupchatbot.data.repository

import com.eallora.breakupchatbot.common.Result
import com.eallora.breakupchatbot.data.local.dao.ProgressDao
import com.eallora.breakupchatbot.data.local.entity.ProgressEntryEntity
import com.eallora.breakupchatbot.domain.model.ProgressEntry
import com.eallora.breakupchatbot.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val progressDao: ProgressDao
) : ProgressRepository {
    
    override fun observeProgressHistory(): Flow<List<ProgressEntry>> =
        progressDao.getHistory(System.currentTimeMillis() - 30 * 24 * 3600 * 1000L)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    
    override fun observeCurrentStreak(): Flow<Int> =
        progressDao.getLatest().let { latest ->
            kotlinx.coroutines.flow.flowOf(latest?.streak ?: 0)
        }
    
    override suspend fun recordProgress(moodScore: Int?, exerciseCount: Int, messageCount: Int) {
        val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        val todayStart = today * 1000 * 60 * 60 * 24
        
        val existing = progressDao.getLatest()
        val entry = ProgressEntryEntity(
            id = existing?.id ?: java.util.UUID.randomUUID().toString(),
            date = todayStart,
            moodScore = moodScore,
            exerciseCount = exerciseCount,
            messageCount = (existing?.messageCount ?: 0) + messageCount,
            streak = if (moodScore != null && moodScore > 5) (existing?.streak ?: 0) + 1 else 0
        )
        progressDao.upsert(entry)
    }
    
    private fun ProgressEntryEntity.toDomain() = ProgressEntry(
        id = id,
        date = date,
        moodScore = moodScore,
        exerciseCount = exerciseCount,
        messageCount = messageCount,
        streak = streak
    )
}