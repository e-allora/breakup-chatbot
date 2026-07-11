package com.eallora.breakupchatbot.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eallora.breakupchatbot.domain.model.AIPersona
import com.eallora.breakupchatbot.domain.model.Message
import com.eallora.breakupchatbot.domain.model.MessageRole
import com.eallora.breakupchatbot.domain.repository.ConversationRepository
import com.eallora.breakupchatbot.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Chat screen.
 */
sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Loaded(
        val conversation: com.eallora.breakupchatbot.domain.model.Conversation?,
        val messages: List<Message>,
        val isSending: Boolean,
        val isLoadingModel: Boolean,
        val isOfflineMode: Boolean,
        val currentPersona: AIPersona,
        val error: String? = null
    ) : ChatUiState
    data class Error(val message: String, val canRetry: Boolean = true) : ChatUiState
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val progressRepository: ProgressRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val conversationId: String? = savedStateHandle.get<String>("conversationId")
    private val _persona = MutableStateFlow(AIPersona.THERAPIST)
    private val _isOfflineMode = MutableStateFlow(false)
    private val _isSending = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ChatUiState> = combine(
        conversationRepository.observeConversation(conversationId ?: ""),
        _persona,
        _isSending,
        _isOfflineMode,
        _error
    ) { conversationWithMessages, persona, isSending, isOfflineMode, error ->
        when {
            conversationWithMessages == null -> ChatUiState.Loading
            else -> ChatUiState.Loaded(
                conversation = conversationWithMessages.conversation,
                messages = conversationWithMessages.messages,
                isSending = isSending,
                isLoadingModel = false,
                isOfflineMode = isOfflineMode,
                currentPersona = persona,
                error = error
            )
        }
    }.catch {
        emit(ChatUiState.Error(it.message ?: "Unknown error"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChatUiState.Loading
    )

    init {
        if (conversationId == null) {
            createNewConversation()
        }
    }

    private fun createNewConversation() {
        viewModelScope.launch {
            conversationRepository.createConversation(_persona.value)
        }
    }

    fun onMessageSend(content: String) {
        val currentConversation = (uiState.value as? ChatUiState.Loaded)?.conversation ?: return
        viewModelScope.launch {
            _isSending.value = true
            _error.value = null

            conversationRepository.sendMessage(content, currentConversation.id)

            // Record progress
            progressRepository.recordProgress(moodScore = null, exerciseCount = 0, messageCount = 1)

            _isSending.value = false
        }
    }

    fun onNewConversationClick() {
        createNewConversation()
    }

    fun onPersonaChange(persona: AIPersona) {
        _persona.value = persona
    }

    fun onRetryMessage() {
        _error.value = null
    }

    fun onOfflineModeChanged(isOffline: Boolean) {
        _isOfflineMode.value = isOffline
    }
}