package com.eallora.breakupchatbot.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eallora.breakupchatbot.domain.model.AIPersona
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * UI state for the Persona Selection screen.
 */
sealed interface PersonaSelectionUiState {
    data object Loading : PersonaSelectionUiState
    data class Error(val message: String) : PersonaSelectionUiState
    data class Success(
        val personas: List<PersonaOption>,
        val selectedPersona: AIPersona?
    ) : PersonaSelectionUiState
}

@HiltViewModel
class PersonaSelectionViewModel @Inject constructor() : ViewModel() {

    val uiState: StateFlow<PersonaSelectionUiState> = flow {
        emit(
            PersonaSelectionUiState.Success(
                personas = listOf(
                    PersonaOption(
                        persona = AIPersona.THERAPIST,
                        title = "Therapist",
                        description = "Clinical, empathetic, uses CBT techniques and validates emotions."
                    ),
                    PersonaOption(
                        persona = AIPersona.FRIEND,
                        title = "Friend",
                        description = "Conversational, supportive, shares relatable experiences."
                    ),
                    PersonaOption(
                        persona = AIPersona.COACH,
                        title = "Coach",
                        description = "Motivational, action-oriented, focuses on goals and progress."
                    )
                ),
                selectedPersona = AIPersona.THERAPIST
            )
        )
    }.catch {
        emit(PersonaSelectionUiState.Error(it.message ?: "Unknown error"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PersonaSelectionUiState.Loading
    )
}