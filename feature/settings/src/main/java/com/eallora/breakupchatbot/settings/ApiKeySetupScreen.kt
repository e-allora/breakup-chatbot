package com.eallora.breakupchatbot.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeySetupScreen(
    viewModel: ApiKeySetupViewModel = hiltViewModel(),
    source: String = "settings",
    onSaved: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var apiKey by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API Key Setup") },
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
            is ApiKeySetupUiState.Idle, is ApiKeySetupUiState.Form -> {
                ApiKeyForm(
                    modifier = Modifier.padding(padding),
                    apiKey = apiKey,
                    onApiKeyChange = { apiKey = it },
                    isSaving = false,
                    error = null,
                    onSaveClick = {
                        viewModel.onSaveClick()
                        onSaved()
                    }
                )
            }
            is ApiKeySetupUiState.Validating -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Validating API key...")
                    }
                }
            }
            is ApiKeySetupUiState.Saved -> {
                LaunchedEffect(Unit) { onSaved() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApiKeyForm(
    modifier: Modifier = Modifier,
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    isSaving: Boolean,
    error: String?,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = apiKey,
            onValueChange = onApiKeyChange,
            label = { Text("API Key") },
            placeholder = { Text("sk-...") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.ui.text.input.KeyboardType.Password.let { KeyboardOptions(keyboardType = it) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your API key is stored securely in Android Keystore and never transmitted to our servers.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = apiKey.isNotBlank() && !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Save API Key")
            }
        }
    }
}