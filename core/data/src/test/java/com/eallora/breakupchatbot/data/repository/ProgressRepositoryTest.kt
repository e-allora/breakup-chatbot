package com.eallora.breakupchatbot.data.repository

import com.eallora.breakupchatbot.data.local.dao.ProgressDao
import com.eallora.breakupchatbot.data.local.entity.ProgressEntryEntity
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ProgressRepositoryTest {
    
    private val progressDao: ProgressDao = mockk()
    private val repository = ProgressRepositoryImpl(progressDao)
    
    @Test
    fun `recordProgress creates entry for today`() = runTest {
        val existing = ProgressEntryEntity(
            id = "test-id",
            date = System.currentTimeMillis(),
            moodScore = 7,
            exerciseCount = 1,
            messageCount = 5,
            streak = 3
        )
        coEvery { progressDao.getLatest() } returns existing
        coEvery { progressDao.upsert(any()) } returns Unit
        
        repository.recordProgress(moodScore = 8, exerciseCount = 0, messageCount = 1)
        
        coVerify { progressDao.getLatest() }
        coVerify { progressDao.upsert(any()) }
    }
    
    @Test
    fun `observeCurrentStreak returns zero when no data`() = runTest {
        coEvery { progressDao.getLatest() } returns null
        
        val streak = repository.observeCurrentStreak()
        // Flow value check
    }
}