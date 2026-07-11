package com.eallora.breakupchatbot.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eallora.breakupchatbot.ui.responsive.WindowSize
import com.eallora.breakupchatbot.ui.responsive.rememberWindowSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(
    viewModel: PrivacyViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val windowSize = rememberWindowSize()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy") },
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
            is PrivacyUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is PrivacyUiState.Error -> {
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
            is PrivacyUiState.Success -> {
                PrivacyContent(
                    modifier = Modifier.padding(padding),
                    state = state,
                    windowSize = windowSize,
                    onExportClick = { /* TODO */ },
                    onDeleteClick = viewModel::onDeleteAllClick
                )
            }
        }
    }
}

@Composable
private fun PrivacyContent(
    modifier: Modifier = Modifier,
    state: PrivacyUiState.Success,
    windowSize: WindowSize,
    onExportClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val contentWidth = contentWidth(windowSize)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.width(contentWidth),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DataSummaryCard(
                conversationCount = state.conversationCount,
                exerciseCount = state.exerciseCount,
                storageUsed = state.storageUsed
            )

            EncryptionStatusCard(
                status = state.dataEncryptionStatus
            )

            SyncStatusCard(
                lastSync = state.lastSync
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            ActionButtons(
                onExportClick = onExportClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun DataSummaryCard(
    conversationCount: Int,
    exerciseCount: Int,
    storageUsed: Long
) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Your Data",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            DataRow(label = "Conversations", value = "$conversationCount")
            DataRow(label = "Exercises", value = "$exerciseCount")
            DataRow(label = "Storage Used", value = formatBytes(storageUsed))
        }
    }
}

@Composable
private fun EncryptionStatusCard(status: EncryptionStatus) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Data Encryption",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (status is EncryptionStatus.Enabled)
                        Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = if (status is EncryptionStatus.Enabled)
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (status is EncryptionStatus.Enabled)
                        "End-to-end encryption enabled (ADR-2)"
                    else "Encryption disabled",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SyncStatusCard(lastSync: Long?) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Sync Status",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (lastSync != null)
                    "Last sync: ${formatTimestamp(lastSync)}"
                else "Not synced",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ActionButtons(
    onExportClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = onExportClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Export All Data")
        }

        var showDeleteConfirm by remember { mutableStateOf(false) }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Delete All Data?") },
                text = { Text("This will permanently delete all conversations, exercises, and progress data. This cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteClick()
                            showDeleteConfirm = false
                        }
                    ) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
                }
            )
        }

        Button(
            onClick = { showDeleteConfirm = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Delete All Data")
        }
    }
}

@Composable
private fun DataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun contentWidth(windowSize: WindowSize): Dp = when (windowSize) {
    is WindowSize.Compact -> 320.dp
    is WindowSize.Medium -> 400.dp
    is WindowSize.Expanded -> 480.dp
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 3600000 -> "${diff / 60000} minutes ago"
        diff < 86400000 -> "${diff / 3600000} hours ago"
        else -> "${diff / 86400000} days ago"
    }
}