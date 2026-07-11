package com.eallora.breakupchatbot.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eallora.breakupchatbot.common.Result
import com.eallora.breakupchatbot.domain.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the API Key Setup screen.
 */
sealed interface ApiKeySetupUiState {
    data object Idle : ApiKeySetupUiState
    data object Validating : ApiKeySetupUiState
    data class Form(
        val apiKey: String = "",
        val isSaving: Boolean = false,
        val error: String? = null,
        val source: String
    ) : ApiKeySetupUiState
    data object Saved : ApiKeySetupUiState
}

@HiltViewModel
class ApiKeySetupViewModel @Inject constructor(
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ApiKeySetupUiState> = MutableStateFlow(ApiKeySetupUiState.Idle)

    fun onApiKeyEntered(key: String) {
        // TODO: Implement key validation
    }

    fun onSaveClick() {
        // TODO: Implement key saving via Android Keystore
    }

    fun onClearClick() {
        // TODO: Implement key clearing
    }
}