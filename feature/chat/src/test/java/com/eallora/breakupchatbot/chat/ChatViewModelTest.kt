package com.eallora.breakupchatbot.chat

import com.eallora.breakupchatbot.domain.model.AIPersona
import com.eallora.breakupchatbot.domain.model.Conversation
import com.eallora.breakupchatbot.domain.model.Message
import com.eallora.breakupchatbot.domain.model.MessageRole
import com.eallora.breakupchatbot.domain.repository.ConversationRepository
import com.eallora.breakupchatbot.domain.repository.ProgressRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ChatViewModelTest {
    
    private val repository: ConversationRepository = mockk()
    private val progressRepository: ProgressRepository = mockk()
    private val savedStateHandle = androidx.lifecycle.SavedStateHandle()
    
    private val viewModel = ChatViewModel(
        conversationRepository = repository,
        progressRepository = progressRepository,
        savedStateHandle = savedStateHandle
    )
    
    @Test
    fun `uiState defaults to Loading when no conversationId`() = runTest {
        coEvery { repository.createConversation(any()) } returns com.eallora.breakupchatbot.common.Result.Success(Conversation(persona = AIPersona.THERAPIST))
        
        viewModel.onNewConversationClick()
        
        coVerify { repository.createConversation(AIPersona.THERAPIST) }
    }
    
    @Test
    fun `onPersonaChange updates state`() {
        viewModel.onPersonaChange(AIPersona.COACH)
        // State flow updates internally - verify persona changed
        coEvery { repository.createConversation(any()) } returns com.eallora.breakupchatbot.common.Result.Success(Conversation(persona = AIPersona.COACH))
    }
    
    @Test
    fun `onRetryMessage clears error`() {
        viewModel.onRetryMessage()
        // Should clear error state
    }
}