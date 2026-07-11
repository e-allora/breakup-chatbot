# Architecture Decision Records (ADRs)

## ADR-1: AI Backend Routing Strategy

**Status:** Accepted  
**Date:** 2026-07-11  
**Deciders:** Architecture Team

### Context

The Breakup Recovery Chatbot requires high-quality conversational AI for therapeutic value, but must also function offline and respect user privacy. Users may have varying technical capabilities - some will obtain cloud API keys for better quality, others need offline-first functionality.

### Decision

Implement a hybrid AI backend strategy with automatic routing:

1. **Primary**: Cloud LLM (OpenAI-compatible API) via user-provided API key stored in Android Keystore
2. **Fallback**: On-device LLM via llama.cpp/MediaPipe when API key unavailable or offline
3. **Routing Logic**: Router evaluates in order: API key exists → connectivity available → send cloud request; else use local model

### Technical Details

```kotlin
class LlmRouter @Inject constructor(
    private val cloudAdapter: CloudLlmAdapter,
    private val localAdapter: LocalLlmAdapter,
    private val apiKeyProvider: ApiKeyProvider,
    private val connectivityChecker: ConnectivityChecker
) {
    suspend fun route(message: String, persona: AIPersona): LlmResponse {
        val hasApiKey = apiKeyProvider.hasApiKey()
        val isConnected = connectivityChecker.isConnected()
        
        return when {
            hasApiKey && isConnected -> cloudAdapter.generate(message, persona)
            else -> localAdapter.generate(message, persona)
        }
    }
}
```

### Consequences

**Positive:**
- Best quality when possible (cloud LLM), functional baseline when not (local)
- User-controlled costs (their API key, their usage)
- Offline functionality maintained for all core features
- No backend server costs for the app developer

**Negative:**
- Increased APK size due to bundled local model (2-4GB)
- Complex initialization logic for local model loading
- Users without API keys get lower quality responses
- Need careful memory management on low-end devices

### Alternatives Considered

| Option | Description | Rejected Because |
|--------|-------------|-----------------|
| Cloud-only | Server-managed LLM backend | Vendor lock-in, ongoing costs, privacy concerns |
| Local-only | Ship with on-device model only | Poor quality, limits accessibility |
| Subscription | App-purchased API access | Recurring costs, billing complexity |

---

## ADR-2: End-to-End Encryption for Account Sync

**Status:** Accepted  
**Date:** 2026-07-11  
**Deciders:** Architecture Team

### Context

Account sync (FR-7) must preserve user privacy. Sensitive conversation data and exercise history should never be readable by the server. Users need cross-device sync without trusting the server with their data.

### Decision

Implement client-side encryption for all synced data using a password-derived key:

1. **Encryption**: AES-256-GCM with keys derived from user password via Argon2id
2. **Key Management**: Password never transmitted to server; server stores only public key for key exchange
3. **Sync Protocol**: Client encrypts data → uploads → downloads encrypted blob → decrypts locally
4. **Recovery**: No password recovery - account deletion and re-registration if password lost

### Technical Details

```kotlin
class E2ESyncManager @Inject constructor(
    private val crypto: CryptoProvider,
    private val syncApi: SyncApi
) {
    suspend fun register(email: String, password: String): User {
        val salt = generateSalt()
        val key = deriveKey(password, salt) // Argon2id
        val publicKey = generateKeyPair(key).public // For device verification
        
        val response = syncApi.register(RegisterRequest(email, publicKey, salt))
        storeEncryptedPrivateKey(response.userId, key)
        return User(response.userId, email, /* ... */)
    }
    
    suspend fun sync(): SyncResult {
        val changes = localDataSource.getPendingChanges()
        val encryptedChanges = changes.map { change ->
            val encrypted = crypto.encrypt(change.serialize(), userKey)
            EncryptedChange(encrypted, change.type, change.timestamp)
        }
        
        val response = syncApi.uploadChanges(encryptedChanges)
        val decryptedChanges = response.changes.map { 
            crypto.decrypt(it.data, userKey).deserialize() 
        }
        
        applyChanges(decryptedChanges)
        return SyncResult.Success
    }
}
```

### Consequences

**Positive:**
- Zero-knowledge server - cannot read user data
- HIPAA/GDPR compliant by design
- Users maintain full data control
- No server-side key management complexity

**Negative:**
- Password loss = permanent data loss
- No "forgot password" recovery flow
- Encryption/decryption adds latency to sync
- Cannot provide server-side search/filtering

### Security Considerations

- TLS 1.3 for all network transport
- Keys stored in Android Keystore with hardware-backed encryption
- IV/nonce generated per encryption operation
- Authentication tokens (JWT) still used for request authentication
- Regular security audit recommended before production

---

## ADR-3: On-Device LLM Model Management

**Status:** Accepted  
**Date:** 2026-07-11  
**Deciders:** Architecture Team

### Context

On-device LLM (via llama.cpp/MediaPipe) provides offline functionality but presents challenges: large model files (2-4GB), slow initialization, memory constraints on older devices, and need for updates.

### Decision

Implement a flexible, downloadable model system:

1. **Model Format**: GGUF format (llama.cpp standard) quantized to reduce size
2. **Model Size Strategy**: Ship with smallest viable model (7B parameter, Q4_K_M quantization ≈ 4.5GB) as download on first launch
3. **Storage**: Download to app-private cache, allow manual cache clearing
4. **Updates**: Check GitHub releases for model updates, download in background
5. **Fallback Chain**: Large model → Smaller model → Error with guidance

### Technical Details

```kotlin
class ModelManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadManager: DownloadManager
) {
    data class ModelConfig(
        val name: String,
        val url: String,
        val sizeBytes: Long,
        val minRamGb: Int,
        val parameterCount: Int
    )
    
    private val models = listOf(
        ModelConfig("medium", "https://models.example.com/7b-q4_k_m.gguf", 4_500_000_000L, 4, 7_000_000_000L),
        ModelConfig("small", "https://models.example.com/3b-q4_k_m.gguf", 2_000_000_000L, 3, 3_000_000_000L)
    )
    
    suspend fun ensureModelAvailable(): ModelLoadResult {
        val deviceRam = getDeviceRamGb()
        val suitableModel = models.firstOrNull { it.minRamGb <= deviceRam }
            ?: return ModelLoadResult.InsufficientStorage
        
        if (!isModelDownloaded(suitableModel.name)) {
            return when {
                hasSufficientStorage(suitableModel.sizeBytes) -> {
                    downloadModel(suitableModel)
                    ModelLoadResult.Downloading
                }
                else -> ModelLoadResult.InsufficientStorage
            }
        }
        
        return ModelLoadResult.Ready
    }
    
    suspend fun initializeLlm(modelName: String): LlmHandle {
        val modelPath = getModelPath(modelName)
        return LlmHandle(LlamaCpp.create(modelPath), modelName)
    }
}

sealed class ModelLoadResult {
    data object Ready : ModelLoadResult()
    data object Downloading : ModelLoadResult()
    data object InsufficientStorage : ModelLoadResult()
    data class Error(val message: String) : ModelLoadResult()
}
```

### Consequences

**Positive:**
- Users download only what they need (based on device capability)
- Model updates don't require app updates
- Clear storage requirements communicated upfront
- Multiple quality points supported

**Negative:**
- First-run experience requires large download (friction)
- Storage warning may deter some users
- Model hosting costs for developer
- Download failures need robust handling

### Download UX Flow

```
First Launch → Storage Check (need 5GB free) → 
Model Download Screen (with progress) → 
Device Capability Check → 
Loading Indicator (<10s) → 
Chat Ready
```

### Model Selection Criteria

| Criterion | Decision |
|-----------|----------|
| 7B Q4_K_M | Default, requires ~4GB RAM, good quality |
| 3B Q4_K_M | For low-end devices, reduced quality acceptable |
| Q5_K_S | Experimental, larger file but better accuracy |
| Update frequency | Every 3 months or major persona improvements |

---

## ADR-4: Persona-Specific Prompt Engineering

**Status:** Accepted  
**Date:** 2026-07-11  
**Deciders:** Architecture Team

### Context

Three distinct AI personas (Therapist, Friend, Coach) require different conversation styles to serve diverse user preferences and recovery needs (US-2).

### Decision

Implement persona-specific prompt templates with system messages:

1. **Therapist**: CBT-grounded, reflective questions, validation-focused
2. **Friend**: Empathetic, informal, supportive without professional distance
3. **Coach**: Action-oriented, goal-focused, motivational

### Technical Details

```kotlin
enum class AIPersona {
    THERAPIST, FRIEND, COACH;
    
    fun getSystemPrompt(): String = when (this) {
        THERAPIST -> """
            You are a compassionate therapist specializing in breakup recovery.
            Use Cognitive Behavioral Therapy techniques. Ask open-ended questions.
            Validate emotions. Help identify cognitive distortions.
            Keep responses under 200 words. Never diagnose.
        """.trimIndent()
        
        FRIEND -> """
            You are a supportive friend who cares deeply.
            Be empathetic and understanding. Share relatable experiences.
            Use casual, warm language. Avoid clinical terms.
            Encourage but don't push. Keep responses conversational.
        """.trimIndent()
        
        COACH -> """
            You are a motivational coach helping rebuild confidence.
            Focus on actionable steps and positive reframing.
            Use energetic, encouraging language. Set small goals.
            Celebrate progress. Be direct and practical.
        """.trimIndent()
    }
}
```

### Consequences

**Positive:**
- Clear user expectation of conversation style
- Consistent behavior across sessions
- Easy to extend with new personas

**Negative:**
- Prompt injection risk if user can modify
- Need continuous tuning for each persona
- Persona drift is possible with certain model behaviors

---

## ADR-5: Conflict Resolution for Account Sync

**Status:** Accepted  
**Date:** 2026-07-11  
**Deciders:** Architecture Team

### Context

When the same data is modified on multiple devices before sync (US-8-4), conflicts must be resolved without data loss.

### Decision

Use last-write-wins with graceful merge for additive changes:

1. **Conversations**: Last update timestamp wins (most recent message state)
2. **Exercises**: Union of all completed exercises (additive)
3. **Messages**: Append-only, preserve all messages
4. **Metadata**: Device capability-based resolution (larger model responses preferred)

### Technical Details

```kotlin
data class SyncConflict(
    val local: LocalChange,
    val remote: EncryptedChange
)

class ConflictResolver {
    fun resolve(conflicts: List<SyncConflict>): ResolutionPlan {
        return conflicts.map { conflict ->
            when (conflict.local.type) {
                ChangeType.MESSAGE -> ResolveStrategy.KeepBoth // Append
                ChangeType.EXERCISE -> ResolveStrategy.KeepBoth // Union
                ChangeType.CONVERSATION -> {
                    if (conflict.local.timestamp > conflict.remote.timestamp)
                        ResolveStrategy.KeepLocal
                    else
                        ResolveStrategy.KeepRemote
                }
            }
        }
    }
}
```

### Consequences

**Positive:**
- Simple implementation
- No user intervention required
- Data preservation for additive changes

**Negative:**
- Potential message duplication on rapid edits
- Last-write-wins may lose intermediate work
- No manual conflict resolution UI