# UX Specification: Breakup Recovery Chatbot

> For Android (Kotlin/Compose/Hilt/Room/Retrofit) — FULL tier with account sync, AI chat with personas, CBT exercises, and analytics. Optimized for phone + tablet responsive layouts.

---

## 1. Personas

### Primary Persona: Priya — Recently Heartbroken
- **Demographics:** 25-35 years old, urban/suburban, college-educated
- **Goals:** Process emotions healthily, rebuild self-esteem, understand what happened, move forward
- **Frustrations:** Feeling overwhelmed by emotions, friends/family offering generic advice, not knowing where to start recovery
- **Context of use:** Uses phone throughout the day, may have limited data or be in situations where privacy matters
- **Technical comfort:** High — comfortable with apps, understands API keys or willing to learn
- **Accessibility needs:** May experience heightened emotional distress; needs calming UI, large touch targets for anxiety-induced motor issues

### Secondary Persona: Marcus — Long-term Recovery Seeker
- **Demographics:** 30-45 years old, may have concurrent therapy, wants supplemental tools
- **Goals:** Track progress over weeks/months, practice CBT techniques, maintain emotional resilience
- **Frustrations:** Wanting structured exercises, needing offline access during travel, wanting data portability
- **Context of use:** Uses both phone and tablet, may have inconsistent internet access
- **Technical comfort:** Medium — can handle setup but prefers simple defaults
- **Accessibility needs:** Standard accessibility requirements, may appreciate larger text/UI for reduced eye strain during vulnerable periods

---

## 2. Screen Catalog & States

### 2.1 Splash Screen
**Route:** `splash`

| State | Data Displayed | Actions |
|-------|---------------|---------|
| `Loading` | App logo, subtle progress indicator | None |
| `NavigateToHome` | None | Auto-navigate to home |
| `NavigateToApiKeySetup` | None | Auto-navigate to API key setup (first launch) |

**Phone Layout:** Centered logo, progress indicator below
**Tablet Layout:** Centered logo with larger scale, progress indicator below

---

### 2.2 API Key Setup Screen
**Route:** `api_key_setup/{source}` (source: `setup`, `settings`, `fallback`)

**UiState Sealed Interface:**
```kotlin
sealed interface ApiKeySetupUiState {
    data object Loading : ApiKeySetupUiState
    data class Form(
        val apiKey: String = "",
        val isSaving: Boolean = false,
        val error: String? = null,
        val source: String
    ) : ApiKeySetupUiState
    data object Saved : ApiKeySetupUiState
}
```

| State | Data Displayed | Actions |
|-------|---------------|---------|
| `Loading` | Minimal UI with shimmer | None |
| `Form` | TextField for API key, save button, info text about key security, link to obtain key | Save, Skip/Dismiss, Learn More |
| `Saved` | Success message, "Continue" button | Continue to home |

**Phone Layout:** Single column, full-width text field, centered content
**Tablet Layout:** Two-pane — left: API key form; right: helpful info panel with screenshots

---

### 2.3 Persona Selector Screen
**Route:** `persona_selector/{source}` (source: `chat`, `settings`)

**UiState Sealed Interface:**
```kotlin
sealed interface PersonaSelectorUiState {
    data object Loading : PersonaSelectorUiState
    data class Success(
        val selectedPersona: Persona?,
        val personas: List<PersonaOption>
    ) : PersonaSelectorUiState
}
data class PersonaOption(
    val id: String,
    val name: String,
    val description: String,
    val stylePreview: String,
    val iconRes: Int
)
```

| State | Data Displayed | Actions |
|-------|---------------|---------|
| `Loading` | Shimmer placeholders for 3 cards | None |
| `Success` | Three persona cards with icons, names, descriptions. Selected state highlighted. | Select persona, Change selection |

**Persona Options:**
1. **Therapist** — Clinical, empathetic, uses CBT techniques, validates emotions
2. **Friend** — Conversational, supportive, shares relatable experiences, casual tone
3. **Coach** — Motivational, action-oriented, focuses on goals and progress

**Phone Layout:** Vertical scroll with full-width cards (56dp min height)
**Tablet Layout:** Grid 2-column layout with expandable detail panel on right when persona selected

---

### 2.4 Chat Screen
**Route:** `chat/{conversationId?}`

**UiState Sealed Interface:**
```kotlin
sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Error(val message: String, val canRetry: Boolean) : ChatUiState
    data class Success(
        val messages: List<MessageUiModel>,
        val isSending: Boolean,
        val isLocalModel: Boolean,
        val persona: Persona,
        val canExport: Boolean
    ) : ChatUiState
}
data class MessageUiModel(
    val id: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long,
    val isStreaming: Boolean = false
)
```

**Screen States:**

| State | Data Displayed | Actions |
|-------|---------------|---------|
| `Loading` | AppBar with persona name, shimmer message list placeholder | None |
| `Error` | Error message, retry button | Retry, New Conversation |
| `Success` (Empty) | Empty chat illustration, prompt suggestions, text input field | Send first message, Select suggestion |
| `Success` (With Messages) | Scrollable message list, input field, persona indicator badge | Send message, Scroll to bottom |
| `Success` (Streaming) | Assistant is typing indicator with animated dots | Cancel generation |

**Message Input States:**
- **Idle:** Text field enabled, send button active when text present
- **Sending:** Text field disabled, send shows loading spinner
- **Offline Mode:** Subtle banner above input indicating local model (`isLocalModel = true`)

**Phone Layout:**
- AppBar: Hamburger menu, persona name, overflow menu
- Message list: Full width bubbles, user messages right-aligned, assistant left-aligned
- Input: Full-width text field with send icon, attachments hidden (future)

**Tablet Layout:**
- Two-pane: Left 240dp — conversation list/history; Right — active chat
- Or single-pane with persistent conversation sidebar (collapsible)
- Larger message bubbles, more padding for readability
- Multi-column message reactions (future) on right side

---

### 2.5 CBT Exercises Screen
**Route:** `exercises`

**UiState Sealed Interface:**
```kotlin
sealed interface ExercisesUiState {
    data object Loading : ExercisesScreenUiState
    data class Success(
        val thoughtRecordCount: Int,
        val behavioralActivationCount: Int,
        val recentExercises: List<RecentExerciseUiModel>,
        val streakDays: Int
    ) : ExercisesScreenUiState
}
data class RecentExerciseUiModel(
    val id: String,
    val type: ExerciseType,
    val title: String,
    val completedAt: Long?,
    val progress: Float // 0f to 1f
)
```

| State | Data Displayed | Actions |
|-------|---------------|---------|
| `Loading` | Shimmer placeholders | None |
| `Success` | Exercise cards grid, streak counter, recent exercises list | Navigate to exercise, View history |

**Exercise Types:**
1. Thought Record
2. Behavioral Activation
3. (Future) Gratitude Journal, Values Assessment

**Phone Layout:** Single column, exercise cards full-width, vertical scroll
**Tablet Layout:** Two-pane — left: exercise catalog grid; right: detailed preview panel

---

### 2.6 Thought Record Screen
**Route:** `exercises/thought_record/{recordId?}`

**UiState Sealed Interface:**
```kotlin
sealed interface ThoughtRecordUiState {
    data object Loading : ThoughtRecordUiState
    data class Form(
        val step: ThoughtRecordStep,
        val data: ThoughtRecordData,
        val isSaving: Boolean,
        val error: String? = null
    ) : ThoughtRecordUiState
    data class Saved(val recordId: String) : ThoughtRecordUiState
}
enum class ThoughtRecordStep {
    SITUATION, EMOTION, AUTOMATIC_THOUGHT,
    EVIDENCE_FOR, EVIDENCE_AGAINST, ALTERNATIVE_THOUGHT,
    OUTCOME, REVIEW
}
```

| State | Data Displayed | Actions |
|-------|---------------|---------|
| `Loading` | Progress indicator | None |
| `Form` | Step-specific content with progress stepper, text inputs, emotion picker | Next, Back, Save, Save Draft |
| `Saved` | Success message, view/edit button, new exercise button | View Exercise, New Exercise |

**Steps:**
1. **Situation:** What happened? (text input)
2. **Emotion:** How did you feel? (emotion wheel or picker)
3. **Automatic Thought:** What went through your mind? (text input)
4. **Evidence For:** Facts supporting the thought (text input)
5. **Evidence Against:** Facts challenging the thought (text input)
6. **Alternative Thought:** More balanced perspective (text input)
7. **Outcome:** How do you feel now? (emotion re-assessment)
8. **Review:** Summary of complete record

**Phone Layout:** Vertical stepper, full-width inputs, bottom navigation buttons
**Tablet Layout:** Multi-step shown in left sidebar, form on right; larger emotion picker

---

### 2.7 Behavioral Activation Screen
**Route:** `exercises/behavioral_activation/{planId?}`

**UiState Sealed Interface:**
```kotlin
sealed interface BehavioralActivationUiState {
    data object Loading : BehavioralActivationUiState
    data class Form(
        val activity: ActivityInput,
        val isSaving: Boolean,
        val suggestedActivities: List<String>
    ) : BehavioralActivationUiState
    data class List(
        val scheduledActivities: List<ScheduledActivityUiModel>,
        val completedToday: List<CompletedActivityUiModel>
    ) : BehavioralActivationUiState
}
```

| State | Data Displayed | Actions |
|-------|---------------|---------|
| `Loading` | Progress indicator | None |
| `Form` | Activity name, scheduled date/time picker, optional notes | Save, Schedule for Today, Schedule for Other |
| `List` | Upcoming activities, completed today section, streak counter | Complete activity, Add new, Reschedule |

**Phone Layout:** Tab layout (Scheduled / Completed), FAB for add, swipe to complete
**Tablet Layout:** Split view — left: calendar/agenda; right: activity detail/form

---

### 2.8 Analytics Screen
**Route:** `analytics`

**UiState Sealed Interface:**
```kotlin
sealed interface AnalyticsUiState {
    data object Loading : AnalyticsUiState
    data object Empty : AnalyticsUiState
    data class Success(
        val moodTrend: List<MoodEntryUiModel>,
        val streakData: StreakUiModel,
        val exerciseCompletion: List<ExerciseStatUiModel>,
        val chatFrequency: List<ChatStatUiModel>
    ) : AnalyticsUiState
}
```

| State | Data Displayed | Actions |
|-------|---------------|---------|
| `Loading` | Shimmer placeholders for charts | None |
| `Empty` | No data illustration, prompt to start chatting/exercises | Start Chat, View Exercises |
| `Success` | Mood trend chart (line), streak counter, exercise completion bars, chat frequency heatmap | Period selector (7/30/90 days), Export Data |

**Phone Layout:** Vertical scroll through charts, period selector in app bar
**Tablet Layout:** Dashboard grid — 2x2 chart layout, detailed tooltips on hover, expanded period selector in sidebar

---

### 2.9 Settings Screen
**Route:** `settings`

**UiState Sealed Interface:**
```kotlin
sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val apiKeyConfigured: Boolean,
        val selectedPersona: Persona?,
        val notificationsEnabled: Boolean,
        val darkMode: DarkModeOption,
        val isSyncEnabled: Boolean,
        val syncStatus: SyncStatus
    ) : SettingsUiState
}
```

| State | Data Displayed | Actions |
|-------|---------------|---------|
| `Loading` | Shimmer list items | None |
| `Success` | Settings categories with toggles and value displays | Navigate to sub-settings |

**Settings Sections:**
1. Account — Sign in/out, sync status, data export
2. AI Preferences — Persona selection, API key management
3. Notifications — Reminders for exercises, streak alerts
4. Appearance — Dark mode, text size, high contrast
5. Privacy — Data storage info, delete all data
6. About — Version, terms, privacy policy, crisis resources

**Phone Layout:** Single column list, section headers, trailing icons/text
**Tablet Layout:** Two-pane — left: navigation list; right: settings detail panel

---

### 2.10 Privacy Dashboard Screen
**Route:** `settings/privacy`

**UiState Sealed Interface:**
```kotlin
sealed interface PrivacyUiState {
    data object Loading : PrivacyUiState
    data class Success(
        val conversationCount: Int,
        val exerciseCount: Int,
        val storageUsed: Long,
        val lastSync: Long?,
        val dataEncryptionStatus: EncryptionStatus
    ) : PrivacyUiState
}
```

| State | Data Displayed | Actions |
|-------|---------------|---------|
| `Loading` | Shimmer placeholders | None |
| `Success` | Data summary cards, clear data options, export button | Delete Conversations, Delete Exercises, Export All, Full Reset |

**Phone Layout:** Card-based summary, destructive actions at bottom with confirmation
**Tablet Layout:** Form layout with data summary on left, actions on right

---

### 2.11 Crisis Resources Screen
**Route:** `crisis_resources`

| State | Data Displayed | Actions |
|-------|---------------|---------|
| `Loading` | Skeleton loaders for resource cards | None |
| `Success` | Crisis hotlines (national + local), crisis chat links, emergency contacts | Call hotline, Open chat link, Save to contacts |

**Resources:**
- National Suicide Prevention Lifeline
- Crisis Text Line
- Emergency Services (911/988)
- Local mental health resources (based on locale)

**Phone Layout:** Full-width resource cards with call button
**Tablet Layout:** Grid layout 2-column, larger touch targets, map view option

---

### 2.12 Account Screen
**Route:** `account`

| State | Data Displayed | Actions |
|-------|---------------|---------|
| `Loading` | Progress indicators | None |
| `SignedOut` | Sign in/up options, benefits of signing in | Sign In, Sign Up, Continue as Guest |
| `SignedIn` | Email, member since date, sync status, device list | Sign Out, Delete Account, Export Data |

**Phone Layout:** Single screen with sign in form or account details
**Tablet Layout:** Modal bottom sheet for sign in, detail view for account management

---

### 2.13 Export Screen
**Route:** `export`

| State | Data Displayed | Actions |
|-------|---------------|---------|
| `Loading` | Export options loading | None |
| `Form` | Format options (JSON, plain text), date range selector, password protection option | Export, Share, Save to Files |

**Phone Layout:** Simple form with export button at bottom
**Tablet Layout:** Side-by-side preview of export data, export controls on right

---

## 3. Navigation Graph

```
[SPLASH]
    │
    ├─(first_launch)→ [API_KEY_SETUP]
    │                    │
    │                    └─→ [HOME_GRAPH]
    │
    └─(has_key)→ [HOME_GRAPH]

[HOME_GRAPH] (Graph)
    │
    ├─[CHAT] {conversationId?} ←──┐
    │    (deep link from notif)    │
    │                               │
    ├─[EXERCISES]                   │
    │    │                          │
    │    ├─[THOUGHT_RECORD] {recordId?}
    │    │    └─→ [THOUGHT_RECORD_DETAIL] {recordId}
    │    │
    │    └─[BEHAVIORAL_ACTIVATION] {planId?}
    │         └─→ [ACTIVITY_DETAIL] {activityId}
    │
    ├─[ANALYTICS]
    │
    └─[SETTINGS]
         │
         ├─[PERSONA_SELECTOR] {source}
         ├─[API_KEY_SETUP] {source}
         ├─[PRIVACY_DASHBOARD]
         ├─[ACCOUNT]
         └─[CRISIS_RESOURCES]

[DEEP LINKS]
    └── breakupchatbot://chat/{conversationId}
    └── breakupchatbot://exercise/thought/{recordId}
    └── breakupchatbot://exercise/behavioral/{planId}
```

**Navigation Transitions:**
- Phone: Standard fade/slide transitions
- Tablet: Crossfade for same-pane, slide for different panes

**Conditional Navigation:**
- First launch → API key setup → Home
- No internet + no API key → Local model banner (not a separate screen)
- Crisis keywords in chat → Non-intrusive banner linking to resources

---

## 4. Theming Tokens

### 4.1 Color System (Material 3)

```
Primary (Comforting Blue)
- primary: #4A6FA5 (buttons, active states)
- onPrimary: #FFFFFF (text on primary)
- primaryContainer: #D6E2F3 (selected states)
- onPrimaryContainer: #141B2A (text on primary container)

Secondary (Healing Green)
- secondary: #6BA86B (positive actions, streaks)
- onSecondary: #FFFFFF
- secondaryContainer: #D9EAD9
- onSecondaryContainer: #1A281A

Tertiary (Warm Coral)
- tertiary: #E76F51 (warnings, important actions)
- onTertiary: #FFFFFF
- tertiaryContainer: #FCE9E1
- onTertiaryContainer: #28120B

Error (Safety Red)
- error: #BA1A1A
- onError: #FFFFFF
- errorContainer: #FFEAEA
- onErrorContainer: #410002

Surfaces
- surface: #FCF8FF (light) / #131018 (dark)
- onSurface: #19171C (light) / #E3DFE9 (dark)
- surfaceVariant: #E1E6ED (light) / #434651 (dark)
- onSurfaceVariant: #434651 (light) / #C4C9D5 (dark)

Backgrounds
- background: #FCF8FF (light) / #131018 (dark)
- onBackground: #19171C (light) / #E3DFE9 (dark)

Outline & Shadows
- outline: #737781
- outlineVariant: #C4C9D5
- surfaceTint: #4A6FA5
```

### 4.2 Typography Scale

| Role | Font | Size | Weight | Line Height | Letter Spacing |
|------|------|------|--------|-------------|--------------|
| Display Large | Roboto Serif | 57sp | Medium | 64sp | -0.25sp |
| Display Medium | Roboto Serif | 45sp | Regular | 52sp | 0sp |
| Display Small | Roboto Serif | 36sp | Regular | 44sp | 0sp |
| Headline Large | Roboto | 32sp | Medium | 40sp | 0sp |
| Headline Medium | Roboto | 28sp | Medium | 36sp | 0sp |
| Headline Small | Roboto | 24sp | Medium | 32sp | 0sp |
| Title Large | Roboto | 22sp | Medium | 28sp | 0sp |
| Title Medium | Roboto | 18sp | Medium | 24sp | 0.1sp |
| Title Small | Roboto | 14sp | Medium | 20sp | 0.1sp |
| Body Large | Roboto | 18sp | Regular | 24sp | 0.5sp |
| Body Medium | Roboto | 16sp | Regular | 20sp | 0.25sp |
| Body Small | Roboto | 14sp | Regular | 16sp | 0.4sp |
| Label Large | Roboto | 14sp | Medium | 20sp | 0.1sp |
| Label Medium | Roboto | 12sp | Medium | 16sp | 0.5sp |
| Label Small | Roboto | 11sp | Medium | 12sp | 0.5sp |

### 4.3 Shape & Elevation

| Component | Shape | Elevation |
|-----------|-------|-----------|
| Buttons (filled) | 100dp (pill) | 1dp |
| Cards | 12dp | 1dp |
| Message bubbles (user) | 16dp top-left, 4dp others | 0dp |
| Message bubbles (assistant) | 16dp top-right, 4dp others | 0dp |
| Input fields | 16dp | 0dp |
| Bottom sheets | 28dp top | 0dp (modal) |
| Dialogs | 28dp | 0dp |

### 4.4 Dark Mode Strategy

- **Dynamic Color (Android 12+):** Use system wallpaper accent for primary
- **Fixed Dark (Android 8-11):** Apply `surface`/`onSurface` inversions as above
- **High Contrast Mode:** Increase contrast ratios to WCAG AAA, add 2dp borders
- **Auto:** Follow system setting, with manual override in settings

---

## 5. Accessibility Checklist

### 5.1 TalkBack Support
- [ ] All interactive elements have descriptive contentDescription
- [ ] Message list uses semantic headings (date separators, message count)
- [ ] Persona selection uses radio button semantics
- [ ] Chat input announces typing state changes
- [ ] CBT exercises announce step changes and validation errors
- [ ] Analytics charts have data point descriptions on focus

### 5.2 Touch Targets
- [ ] All buttons ≥ 48dp × 48dp
- [ ] List items ≥ 48dp height
- [ ] Icon buttons padded to 48dp minimum
- [ ] Message bubbles have invisible 48dp touch extenders for reactions
- [ ] Navigation rail items 56dp minimum

### 5.3 Color & Contrast
- [ ] All text meets WCAG 2.1 AA (4.5:1) on standard surfaces
- [ ] Interactive states meet 3:1 contrast against background
- [ ] High contrast mode available (WCAG AAA 7:1)
- [ ] Color is not the only indicator of state (icons + text)
- [ ] Emotion picker uses both color and label

### 5.4 Keyboard Navigation
- [ ] All interactive elements reachable via Tab
- [ ] Chat input can be focused on load
- [ ] CBT forms follow logical tab order
- [ ] Dropdown menus navigable with arrow keys
- [ ] Escape key closes modals/bottom sheets

### 5.5 Motion & Animation
- [ ] Animations respect system "Remove animations" setting
- [ ] Message send/receive uses `animateContentSize` with Duration.Short
- [ ] State transitions use `AnimatedVisibility`
- [ ] No autoplay animations in analytics
- [ ] Streaming indicator uses subtle pulse, not rapid flashing

### 5.6 Text Scaling
- [ ] All layouts support font scale up to 2.0x
- [ ] Chat bubbles expand with longer text
- [ ] CBT forms scroll vertically when content overflows
- [ ] Analytics charts maintain readability with larger fonts
- [ ] No text truncation at maximum scale

---

## 6. Phone vs Tablet Adaptations

### 6.1 Breakpoints
```
Phone: < 600dp width (default)
Tablet: ≥ 600dp width
Large Tablet: ≥ 840dp width
```

### 6.2 Navigation Adaptations

| Feature | Phone | Tablet |
|---------|-------|--------|
| Main Nav | Bottom navigation bar | Navigation rail (left) or split-view sidebar |
| Chat History | Drawer menu (hamburger) | Persistent conversation list pane (240dp) |
| Exercises | Single list view | Two-pane catalog + detail |
| Analytics | Vertical scroll | Dashboard grid (2×2 charts) |
| Settings | Single list | Navigation + detail panes |
| Persona Selection | Full-screen picker | Modal sheet or side panel |

### 6.3 Layout Adaptations

| Screen | Phone Adaptations | Tablet Adaptations |
|--------|-------------------|-------------------|
| Chat | Single pane, full width messages | Two-pane (conv list + chat), wider message area |
| Thought Record | Stepper vertical, form full width | Left step list, right form panel |
| Behavioral Activation | Tabs at top, list below | Side-by-side calendar + activity list |
| Analytics | Stacked charts vertically | Grid layout, tooltips on hover |
| Settings | Single column list | Master-detail split |
| Crisis Resources | Full-width cards | Grid layout, larger CTAs |

### 6.4 Input Adaptations

| Element | Phone | Tablet |
|---------|-------|--------|
| Keyboard | Full screen, input at bottom | Inline with 48dp margin from edge |
| Text Field | Full width | Max 640dp width centered |
| Picker Dialogs | Bottom sheets | Center dialogs or inline pickers |
| Multi-select | Modal checklist | Inline checkbox list |

---

## 7. Motion & Transitions Spec

### 7.1 Standard Transitions
- **Screen enter/exit:** `slideInHorizontally` + `fadeOut` (Phone), `fadeIn` (Tablet)
- **Modal enter:** `scaleIn` + `fadeIn`
- **Bottom sheet:** `slideInVertically` from bottom

### 7.2 Component Transitions
- **Message send:** Scale from 0.8 to 1.0 with fade
- **Message receive:** Slide in from left + fade
- **Streaming response:** Typewriter effect with `animateContentSize`
- **Button press:** `scaleIn` to 0.95
- **Toggle switch:** Smooth 200ms transition

### 7.3 Eased Curves
- **Standard:** `Easing.Emphasized` (300ms)
- **Decorated:** `Easing.EmphasizedDecelerated` (250ms) for decorative
- **Accelerated:** `Easing.Linear` (150ms) for micro-interactions

---

## 8. Error States & Empty States

### 8.1 Empty States
| Screen | Illustration | Text | CTA |
|--------|--------------|------|-----|
| Chat | Broken heart healing | "No messages yet. How are you feeling today?" | Text input prompt |
| Exercises | Planner with checkmarks | "No exercises completed. Start with a thought record." | Start Thought Record |
| Analytics | Chart with upward trend | "Not enough data yet. Keep using the app to see trends." | Start Chat |
| Account | Person with lock | "Sign in to sync across devices." | Sign In |

### 8.2 Error States
| Type | Message | Recovery |
|------|---------|----------|
| Network | "Can't connect. Using offline mode." | Retry button, offline indicator |
| API Key Invalid | "API key not working. Check settings." | Open Settings, Use Local Model |
| Storage Full | "Not enough storage for local model." | Free up space, Use Cloud |
| Sync Failed | "Couldn't sync. Will retry automatically." | Retry Now, Learn More |

---

## 9. Implementation Notes for Developer

### 9.1 Reusable Composables
- `ChatMessageBubble(message: MessageUiModel)` — Handles user/assistant styling
- `PersonaCard(persona: PersonaOption, selected: Boolean)` — Consistent persona selection
- `ProgressStepper(steps: List<Step>, current: Step)` — CBT exercise navigation
- `EmotionPicker(selected: Emotion?, onSelect: (Emotion) → Unit)` — Standardized emotion selection
- `ChartDataPoint(value: Float, label: String)` — Accessible chart elements
- `OfflineBanner(isVisible: Boolean)` — Shows local model indicator

### 9.2 Configuration Flags
- `isTablet: Boolean` — Derived from screen width breakpoint
- `isLocalModel: Boolean` — Determined by API key availability + connectivity
- `highContrastMode: Boolean` — From system settings or user preference

### 9.3 Strings to Define (strings.xml)
- All persona names and descriptions
- CBT exercise instructions and validation messages
- Error messages with recovery guidance
- Privacy dashboard explanations
- Crisis resource descriptions

---

*End of UX Specification — Ready for developer implementation*