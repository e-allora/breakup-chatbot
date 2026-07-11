package com.eallora.breakupchatbot.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eallora.breakupchatbot.domain.model.AIPersona
import com.eallora.breakupchatbot.domain.model.Message
import com.eallora.breakupchatbot.domain.model.MessageRole
import com.eallora.breakupchatbot.ui.components.MessageBubble
import com.eallora.breakupchatbot.ui.components.MessageInput
import com.eallora.breakupchatbot.ui.responsive.WindowSize
import com.eallora.breakupchatbot.ui.responsive.rememberWindowSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateToExercises: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val windowSize = rememberWindowSize()
    var messageText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            ChatTopBar(
                uiState = uiState,
                onNavigateToExercises = onNavigateToExercises,
                onNavigateToAnalytics = onNavigateToAnalytics,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is ChatUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ChatUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (state.canRetry) {
                            Button(onClick = viewModel::onRetryMessage) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
            is ChatUiState.Loaded -> {
                ChatContent(
                    modifier = Modifier.padding(padding),
                    messages = state.messages,
                    isSending = state.isSending,
                    isOfflineMode = state.isOfflineMode,
                    persona = state.currentPersona,
                    messageText = messageText,
                    onMessageChange = { messageText = it },
                    onSendClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.onMessageSend(messageText)
                            messageText = ""
                        }
                    },
                    windowSize = windowSize
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    uiState: ChatUiState,
    onNavigateToExercises: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                when (uiState) {
                    is ChatUiState.Loaded -> uiState.currentPersona.getTitle()
                    else -> "Chat"
                }
            )
        },
        navigationIcon = if (onNavigateBack != null) {
            {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        } else {
            null
        },
        actions = {
            IconButton(onClick = onNavigateToExercises) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = "Exercises"
                )
            }
            IconButton(onClick = onNavigateToAnalytics) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Analytics"
                )
            }
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )
}

@Composable
private fun ChatContent(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    isSending: Boolean,
    isOfflineMode: Boolean,
    persona: AIPersona,
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    windowSize: WindowSize
) {
    val listState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {
        if (messages.isEmpty()) {
            EmptyChatView(persona = persona)
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = 80.dp,
                    start = horizontalPadding(windowSize),
                    end = horizontalPadding(windowSize)
                )
            ) {
                items(messages) { message ->
                    MessageBubble(
                        content = message.content,
                        isUser = message.role == com.eallora.breakupchatbot.domain.model.MessageRole.USER,
                        timestamp = message.timestamp,
                        isStreaming = false
                    )
                }
            }
        }

        if (isOfflineMode) {
            OfflineBanner()
        }

        MessageInput(
            message = messageText,
            onMessageChange = onMessageChange,
            onSendClick = onSendClick,
            isSending = isSending,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun EmptyChatView(persona: AIPersona) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Start a conversation with your ${persona.getTitle()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OfflineBanner() {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(
            text = "Offline mode: Using local AI model",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun horizontalPadding(windowSize: WindowSize): Dp = when (windowSize) {
    is WindowSize.Compact -> 0.dp
    is WindowSize.Medium -> 32.dp
    is WindowSize.Expanded -> 64.dp
}