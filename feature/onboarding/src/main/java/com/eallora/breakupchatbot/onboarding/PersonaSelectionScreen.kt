package com.eallora.breakupchatbot.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eallora.breakupchatbot.domain.model.AIPersona
import com.eallora.breakupchatbot.ui.responsive.WindowSize
import com.eallora.breakupchatbot.ui.responsive.rememberWindowSize

data class PersonaOption(
    val persona: AIPersona,
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaSelectionScreen(
    viewModel: PersonaSelectionViewModel = hiltViewModel(),
    source: String = "chat",
    onPersonaSelected: (AIPersona) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val windowSize = rememberWindowSize()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Persona") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is PersonaSelectionUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is PersonaSelectionUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is PersonaSelectionUiState.Success -> {
                PersonaContent(
                    modifier = Modifier.padding(padding),
                    personas = state.personas,
                    selectedPersona = state.selectedPersona,
                    windowSize = windowSize,
                    onPersonaSelected = onPersonaSelected
                )
            }
        }
    }
}

@Composable
private fun PersonaContent(
    modifier: Modifier = Modifier,
    personas: List<PersonaOption>,
    selectedPersona: AIPersona?,
    windowSize: WindowSize,
    onPersonaSelected: (AIPersona) -> Unit
) {
    val useTwoPane = windowSize.isMedium || windowSize.isExpanded

    if (useTwoPane) {
        TwoPanePersonaLayout(
            personas = personas,
            selectedPersona = selectedPersona,
            onPersonaSelected = onPersonaSelected,
            modifier = modifier
        )
    } else {
        SinglePanePersonaLayout(
            personas = personas,
            selectedPersona = selectedPersona,
            onPersonaSelected = onPersonaSelected,
            modifier = modifier
        )
    }
}

@Composable
private fun SinglePanePersonaLayout(
    personas: List<PersonaOption>,
    selectedPersona: AIPersona?,
    onPersonaSelected: (AIPersona) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(personas) { option ->
            PersonaCard(
                option = option,
                isSelected = option.persona == selectedPersona,
                onClick = { onPersonaSelected(option.persona) }
            )
        }
    }
}

@Composable
private fun TwoPanePersonaLayout(
    personas: List<PersonaOption>,
    selectedPersona: AIPersona?,
    onPersonaSelected: (AIPersona) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(personas) { option ->
                PersonaCard(
                    option = option,
                    isSelected = option.persona == selectedPersona,
                    onClick = { onPersonaSelected(option.persona) }
                )
            }
        }

        if (selectedPersona != null) {
            PersonaDetailPanel(
                persona = selectedPersona,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun PersonaCard(
    option: PersonaOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = option.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = option.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PersonaDetailPanel(persona: AIPersona, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = persona.getTitle(),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = when (persona) {
                    AIPersona.THERAPIST -> "Provides clinical approach using CBT techniques, reflective questions, and emotional validation."
                    AIPersona.FRIEND -> "Conversational and supportive, sharing relatable experiences in a casual tone."
                    AIPersona.COACH -> "Action-oriented guidance focused on goals, motivation, and practical steps."
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}