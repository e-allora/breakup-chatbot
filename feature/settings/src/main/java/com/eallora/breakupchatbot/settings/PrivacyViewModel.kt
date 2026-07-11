package com.eallora.breakupchatbot.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eallora.breakupchatbot.domain.repository.ConversationRepository
import com.eallora.breakupchatbot.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Privacy Dashboard screen.
 */
sealed interface PrivacyUiState {
    data object Loading : PrivacyUiState
    data class Success(
        val conversationCount: Int,
        val exerciseCount: Int,
        val storageUsed: Long,
        val lastSync: Long?,
        val dataEncryptionStatus: EncryptionStatus
    ) : PrivacyUiState
    data class Error(val message: String) : PrivacyUiState
}

/**
 * Encryption status for privacy dashboard.
 */
sealed interface EncryptionStatus {
    data object Enabled : EncryptionStatus
    data object Disabled : EncryptionStatus
}

@HiltViewModel
class PrivacyViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    val uiState: StateFlow<PrivacyUiState> = flow {
        // TODO: Implement actual data retrieval
        emit(
            PrivacyUiState.Success(
                conversationCount = 0,
                exerciseCount = 0,
                storageUsed = 0L,
                lastSync = null,
                dataEncryptionStatus = EncryptionStatus.Enabled
            )
        )
    }.catch {
        emit(PrivacyUiState.Error(it.message ?: "Unknown error"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PrivacyUiState.Loading
    )

    fun onDeleteAllClick() {
        // TODO: Implement data deletion
    }
}