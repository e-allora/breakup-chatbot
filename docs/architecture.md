# Architecture Plan: Breakup Recovery Chatbot

## 1. Module Structure

### Module Tree

```
:breakupchatbot (app module)
├── :core:common          # Shared utilities, extensions, Result wrapper
├── :core:ui              # Compose components, themes, dimens, composables
├── :core:domain          # Entities, use cases, repository interfaces (no Android deps)
├── :core:data            # Room DB, Retrofit APIs, repository implementations
├── :feature:chat          # Chat screen, personas, message composition
├── :feature:exercises     # CBT exercises (Thought Record, Behavioral Activation)
├── :feature:analytics     # Progress visualization, streak tracking
├── :feature:settings      # API key config, privacy, account sync
└── :feature:onboarding    # Initial persona selection, setup flows
```

### Dependency Rules

| Module | Can Depend On | Cannot Depend On |
|--------|---------------|------------------|
| `:core:common` | Nothing | Everything |
| `:core:ui` | `:core:common` | Android framework, Room, Retrofit |
| `:core:domain` | `:core:common` | Android framework, Room, Retrofit, Compose |
| `:core:data` | `:core:domain`, `:core:common` | Compose, feature modules |
| `feature:*` | `:core:domain`, `:core:ui`, `:core:common` | `:core:data`, other features |

## 2. Layer Conventions

### Clean Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│  Presentation (feature modules - ViewModels + Composables)│
├─────────────────────────────────────────────────────────┤
│  Domain (entities + use cases + repository interfaces)   │
├─────────────────────────────────────────────────────────┤
│  Data (Room entities/DAOs, Retrofit APIs, repos)        │
└─────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

- **Presentation**: UI state, user events, ViewModel orchestration
- **Domain**: Business logic, pure entities, use cases, abstractions
- **Data**: Data persistence, network calls, external system integration

## 3. DI Graph (Hilt)

### Hilt Modules

```kotlin
// Core modules
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase
    @Provides fun provideConversationDao(db: AppDatabase): ConversationDao
    @Provides fun provideExerciseDao(db: AppDatabase): ExerciseDao
    @Provides fun provideProgressDao(db: AppDatabase): ProgressDao
    @Provides fun provideUserDao(db: AppDatabase): UserDao
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton fun provideRetrofit(): Retrofit
    @Provides fun provideSyncApi(retrofit: Retrofit): SyncApi
}

@Module
@InstallIn(SingletonComponent::class)
object EncryptionModule {
    @Provides @Singleton fun provideEncryptedStorage(@ApplicationContext ctx: Context): EncryptedSharedPreferences
    @Provides @Singleton fun provideKeyStore(): AndroidKeyStore
}

@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    @Provides @Singleton fun provideCloudLlmAdapter(apiKeyProvider: ApiKeyProvider): CloudLlmAdapter
    @Provides @Singleton fun provideLocalLlmAdapter(@ApplicationContext ctx: Context): LocalLlmAdapter
    @Provides @Singleton fun provideLlmRouter(
        cloud: CloudLlmAdapter,
        local: LocalLlmAdapter,
        apiKeyProvider: ApiKeyProvider
    ): LlmRouter
}
```

### Scoped Components

- `SingletonComponent`: Database, Retrofit, Encryption, LLM adapters
- `ActivityComponent`: Activity-level ViewModels
- `ViewModelComponent`: Screen-level ViewModels (default scope)

## 4. Data Layer Design

### Room Entities

```kotlin
@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val persona: AIPersona,
    val createdAt: Long,
    val updatedAt: Long,
    val title: String? = null,
    val isArchived: Boolean = false
)

@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(
        entity = Conversation::class,
        parentColumns = ["id"],
        childColumns = ["conversationId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Message(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val content: String,
    val role: MessageRole, // USER, ASSISTANT, SYSTEM
    val timestamp: Long,
    val isFromLocalModel: Boolean = false
)

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: ExerciseType, // THOUGHT_RECORD, BEHAVIORAL_ACTIVATION
    val createdAt: Long,
    val completedAt: Long? = null,
    val persona: AIPersona? = null
)

@Entity(tableName = "thought_records")
data class ThoughtRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val situation: String,
    val emotion: String,
    val automaticThought: String,
    val evidenceFor: String,
    val evidenceAgainst: String,
    val alternativeThought: String,
    val timestamp: Long
)

@Entity(tableName = "activities")
data class ScheduledActivity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val activityName: String,
    val scheduledTime: Long,
    val completedAt: Long? = null,
    val notes: String? = null
)

@Entity(tableName = "progress_entries")
data class ProgressEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val date: Long, // Day timestamp
    val moodScore: Int? = null, // 1-10 scale
    val exerciseCount: Int,
    val messageCount: Int,
    val streak: Int
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val email: String,
    val createdAt: Long,
    val lastSyncAt: Long? = null,
    val deviceId: String
)
```

### DAOs

```kotlin
@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE isArchived = 0 ORDER BY updatedAt DESC")
    fun getAllActive(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    fun getById(id: String): Flow<Conversation?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conversation: Conversation)

    @Update
    suspend fun update(conversation: Conversation)

    @Delete
    suspend fun delete(conversation: Conversation)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getByConversation(conversationId: String): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<Message>)

    @Delete
    suspend fun deleteMessage(message: Message)
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE completedAt IS NOT NULL ORDER BY completedAt DESC")
    fun getCompleted(): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: Exercise)

    @Update
    suspend fun update(exercise: Exercise)
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM progress_entries WHERE date >= :fromDate ORDER BY date ASC")
    fun getHistory(fromDate: Long): Flow<List<ProgressEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: ProgressEntry)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrent(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)
}
```

### Retrofit APIs/DTOs

```kotlin
// Sync API for account sync
interface SyncApi {
    @GET("sync/chunks")
    suspend fun getChanges(
        @Header("Authorization") token: String,
        @Query("since") timestamp: Long
    ): SyncResponse

    @POST("sync/chunks")
    suspend fun uploadChanges(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): SyncResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}

// Request/Response models
@Serializable
data class SyncRequest(
    val deviceId: String,
    val changes: List<EncryptedChange>
)

@Serializable
data class SyncResponse(
    val changes: List<EncryptedChange>
)

@Serializable
data class RegisterRequest(
    val email: String,
    val publicKey: String // For E2E encryption
)

@Serializable
data class AuthResponse(
    val token: String,
    val userId: String
)
```

### Repository Pattern

```kotlin
// Domain interfaces
interface ConversationRepository {
    fun observeConversation(id: String): Flow<ConversationWithMessages>
    fun observeActiveConversations(): Flow<List<Conversation>>
    suspend fun createConversation(persona: AIPersona): Conversation
    suspend fun sendMessage(message: Message): Result<Message>
    suspend fun archiveConversation(id: String)
}

interface ExerciseRepository {
    fun observeExercises(): Flow<List<Exercise>>
    suspend fun saveThoughtRecord(record: ThoughtRecord): Result<Unit>
    suspend fun saveActivity(activity: ScheduledActivity): Result<Unit>
}

interface ProgressRepository {
    fun observeProgressHistory(): Flow<List<ProgressEntry>>
    fun observeCurrentStreak(): Flow<Int>
}

interface SyncRepository {
    suspend fun register(email: String): Result<User>
    suspend fun syncPendingChanges(): Result<SyncResult>
    suspend fun decryptAndApplyServerChanges(changes: List<EncryptedChange>)
}
```

## 5. Presentation Contracts

### Chat Screen

```kotlin
// UiState
sealed interface ChatUiState {
    data class Loading(val conversationId: String) : ChatUiState
    data class Loaded(
        val messages: List<Message>,
        val isSending: Boolean,
        val isLoadingModel: Boolean,
        val isOfflineMode: Boolean,
        val currentPersona: AIPersona
    ) : ChatUiState
    data class Error(val message: String) : ChatUiState
}

// ViewModel public API
class ChatViewModel(
    private val conversationId: String,
    private val sendMessageUseCase: SendMessageUseCase,
    private val loadConversationUseCase: LoadConversationUseCase
) : ViewModel() {
    val uiState: StateFlow<ChatUiState> = ...
    
    fun onMessageSend(content: String)
    fun onNewConversationClick()
    fun onLoadMoreClick() // for pagination
    fun onPersonaChange(persona: AIPersona)
}

// Screen composable signature
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateToExercises: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSettings: () -> Unit
)
```

### Persona Selection Screen

```kotlin
sealed interface PersonaSelectionUiState {
    data object Loading : PersonaSelectionUiState
    data class Loaded(
        val personas: List<AIPersona>,
        val selectedPersona: AIPersona?,
        val hasApiKey: Boolean
    ) : PersonaSelectionUiState
}

class PersonaSelectionViewModel(
    private val getPersonasUseCase: GetPersonasUseCase,
    private val setDefaultPersonaUseCase: SetDefaultPersonaUseCase
) : ViewModel() {
    val uiState: StateFlow<PersonaSelectionUiState> = ...
    fun onPersonaSelected(persona: AIPersona)
}

@Composable
fun PersonaSelectionScreen(
    viewModel: PersonaSelectionViewModel = hiltViewModel(),
    onPersonaSelected: (AIPersona) -> Unit
)
```

### CBT Exercises Screen

```kotlin
sealed interface ExercisesUiState {
    data object Loading : ExercisesUiState
    data class Loaded(
        val exercises: List<ExerciseHistoryItem>,
        val suggestedExercises: List<SuggestedExercise>
    ) : ExercisesUiState
}

class ExercisesViewModel(
    private val getExercisesUseCase: GetExercisesUseCase,
    private val getSuggestionsUseCase: GetSuggestionsUseCase
) : ViewModel() {
    val uiState: StateFlow<ExercisesUiState> = ...
    fun onStartExercise(exerciseType: ExerciseType)
}

@Composable
fun ExercisesScreen(
    viewModel: ExercisesViewModel = hiltViewModel(),
    onNavigateToThoughtRecord: () -> Unit,
    onNavigateToBehavioralActivation: () -> Unit
)
```

### Thought Record Exercise Screen

```kotlin
sealed interface ThoughtRecordUiState {
    data object Loading : ThoughtRecordUiState
    data class Editing(
        val step: ThoughtRecordStep, // SITUATION, EMOTION, AUTOMATIC_THOUGHT, EVIDENCE, ALTERNATIVE, REVIEW
        val currentData: ThoughtRecordData,
        val validationErrors: Map<String, String>
    ) : ThoughtRecordUiState
    data class Error(val message: String) : ThoughtRecordUiState
}

data class ThoughtRecordData(
    val situation: String = "",
    val emotion: String = "",
    val automaticThought: String = "",
    val evidenceFor: String = "",
    val evidenceAgainst: String = "",
    val alternativeThought: String = ""
)

class ThoughtRecordViewModel(
    private val saveThoughtRecordUseCase: SaveThoughtRecordUseCase
) : ViewModel() {
    val uiState: StateFlow<ThoughtRecordUiState> = ...
    fun onNextStep(data: ThoughtRecordData)
    fun onPreviousStep()
    fun onSubmit(data: ThoughtRecordData)
    fun onFieldChange(field: String, value: String)
}

@Composable
fun ThoughtRecordScreen(
    viewModel: ThoughtRecordViewModel = hiltViewModel(),
    onCompleted: () -> Unit
)
```

### Analytics Screen

```kotlin
sealed interface AnalyticsUiState {
    data object Loading : AnalyticsUiState
    data class Loaded(
        val moodTrend: List<MoodTrendEntry>,
        val streakCount: Int,
        val exerciseCompletionRate: Float,
        val weeklyActivity: Map<String, Int> // day -> count
    ) : AnalyticsUiState
}

class AnalyticsViewModel(
    private val getProgressHistoryUseCase: GetProgressHistoryUseCase,
    private val getStreakUseCase: GetStreakUseCase
) : ViewModel() {
    val uiState: StateFlow<AnalyticsUiState> = ...
}

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
)
```

### Settings Screen

```kotlin
sealed interface SettingsUiState {
    data class Loaded(
        val apiKeyConfigured: Boolean,
        val isAccountSignedIn: Boolean,
        val accountEmail: String?,
        val syncStatus: SyncStatus?,
        val currentPersona: AIPersona
    ) : SettingsUiState
}

class SettingsViewModel(
    private val getApiKeyStatusUseCase: GetApiKeyStatusUseCase,
    private val clearAllDataUseCase: ClearAllDataUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = ...
    fun onApiKeyChangeClick()
    fun onSignOutClick()
    fun onDeleteAllDataClick()
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToApiKeySetup: () -> Unit
)
```

### API Key Setup Screen

```kotlin
sealed interface ApiKeySetupUiState {
    data object Idle : ApiKeySetupUiState
    data object Validating : ApiKeySetupUiState
    data class Error(val message: String) : ApiKeySetupUiState
}

class ApiKeySetupViewModel(
    private val validateApiKeyUseCase: ValidateApiKeyUseCase,
    private val saveApiKeyUseCase: SaveApiKeyUseCase
) : ViewModel() {
    val uiState: StateFlow<ApiKeySetupUiState> = ...
    fun onApiKeyEntered(key: String)
    fun onSaveClick()
    fun onClearClick()
}

@Composable
fun ApiKeySetupScreen(
    viewModel: ApiKeySetupViewModel = hiltViewModel(),
    onSaved: () -> Unit
)
```

## 6. Navigation Graph

```kotlin
// NavGraph.kt
@Composable
fun BreakupChatbotNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController, startDestination = Screen.Chat.route) {
        composable(Screen.Chat.route) {
            ChatScreen(
                onNavigateToExercises = { navController.navigate(Screen.Exercises.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Exercises.route) { ExercisesScreen(/* ... */) }
        composable(Screen.Analytics.route) { AnalyticsScreen() }
        composable(Screen.Settings.route) { SettingsScreen(/* ... */) }
        composable(Screen.ApiKeySetup.route) { ApiKeySetupScreen(/* ... */) }
        composable(Screen.ThoughtRecord.createRoute()) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId")
            ThoughtRecordScreen(/* ... */)
        }
    }
}

sealed class Screen(val route: String) {
    data object Chat : Screen("chat")
    data object Exercises : Screen("exercises")
    data object Analytics : Screen("analytics")
    data object Settings : Screen("settings")
    data object ApiKeySetup : Screen("api_key_setup")
    data object PersonaSelection : Screen("persona_selection")
    data object ThoughtRecord : Screen("thought_record/{exerciseId}") {
        fun createRoute(exerciseId: String) = "thought_record/$exerciseId"
    }
}
```

## 7. Responsive Design Strategy

### Window Size Classes

```kotlin
sealed class WindowSize {
    data class Compact(val width: Dp) : WindowSize()
    data class Medium(val width: Dp) : WindowSize()
    data class Expanded(val width: Dp) : WindowSize()
}

@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp < 600 -> WindowSize.Compact(configuration.screenWidthDp.dp)
        configuration.screenWidthDp < 840 -> WindowSize.Medium(configuration.screenWidthDp.dp)
        else -> WindowSize.Expanded(configuration.screenWidthDp.dp)
    }
}
```

### Tablet Layout Variants

| Screen | Phone (Compact) | Tablet (Medium/Expanded) |
|--------|-----------------|--------------------------|
| Chat | Single pane | Two pane (conversations list + chat) |
| Exercises | List → Detail navigation | Master-detail split view |
| Analytics | Simple charts | Expanded dashboard with multiple chart views |
| Settings | Full screen | Side sheet or condensed view |

---

*Document prepared for the Android Development Suite — consumed by android-development-skill*