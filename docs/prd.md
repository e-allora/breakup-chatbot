# PRD: Breakup Recovery Chatbot

## Problem Statement

Going through a breakup is a universal human experience marked by emotional upheaval, loss of identity, and uncertainty about the future. Most people lack structured support during this vulnerable period—they either rely on friends who may not have the right tools, or they search online for scattered, unpersonalized advice. Traditional therapy is expensive and has waiting lists, while self-help books feel impersonal. There's a gap for immediate, accessible, personalized emotional support that guides users through evidence-based recovery practices.

**Who has this problem:** Adults (18+) experiencing romantic breakup, particularly those who:
- Feel isolated without adequate support systems
- Want private, non-judgmental space to process emotions
- Need structured guidance but cannot afford or access therapy
- Prefer digital/always-available support over scheduled appointments

**Why now:** Mental health awareness is rising, but access to care remains limited. AI technology has matured to provide personalized, conversational support while maintaining privacy through on-device inference. The stigma around mental health support makes an anonymous, mobile-first solution particularly valuable.

---

## Target Users

### Primary Persona: "Priya" - Recently Heartbroken

- **Demographics:** 25-35 years old, urban/suburban, college-educated
- **Goals:** Process emotions healthily, rebuild self-esteem, understand what happened, move forward
- **Frustrations:** Feeling overwhelmed by emotions, friends/family offering generic advice, not knowing where to start recovery
- **Context of use:** Uses phone throughout the day, may have limited data or be in situations where privacy matters
- **Technical comfort:** High - comfortable with apps, understands API keys or willing to learn
- **Accessibility needs:** May experience heightened emotional distress; needs calming UI, large touch targets for anxiety-induced motor issues

### Secondary Persona: "Marcus" - Long-term Recovery Seeker

- **Demographics:** 30-45 years old, may have concurrent therapy, wants supplemental tools
- **Goals:** Track progress over weeks/months, practice CBT techniques, maintain emotional resilience
- **Frustrations:** Wanting structured exercises, needing offline access during travel, wanting data portability
- **Context of use:** Uses both phone and tablet, may have inconsistent internet access
- **Technical comfort:** Medium - can handle setup but prefers simple defaults
- **Accessibility needs:** Standard accessibility requirements, may appreciate larger text/UI for reduced eye strain during vulnerable periods

---

## Goals & Success Metrics

| Goal | Metric | Target |
|------|--------|--------|
| User engagement | Daily active users (DAU) / Monthly active users (MAU) ratio | ≥ 40% |
| Core value delivery | Users completing at least 1 full CBT exercise | ≥ 30% within first week |
| Retention | 7-day retention rate | ≥ 60% |
| Retention | 30-day retention rate | ≥ 35% |
| Offline capability | Offline conversation sessions per user per week | ≥ 2 for users with spotty connectivity |
| Privacy satisfaction | Users rating privacy as "good" or "excellent" (survey) | ≥ 80% |
| Progression | Users unlocking at least 3 recovery milestones | ≥ 25% within 2 weeks |
| Crash-free experience | Crash-free sessions rate | ≥ 99.5% |

---

## Out of Scope

- **Not a crisis intervention tool:** Does not handle suicidal ideation or severe mental health crises; prominently links to crisis hotlines
- **No human therapist matching:** Does not connect users to actual therapists (out of scope for MVP)
- **No social features:** Does not allow users to connect with each other (privacy/compliance concerns)
- **No voice/video calls:** Text-only for MVP; voice features may come later
- **No diagnostic capabilities:** Does not diagnose depression, anxiety, or other conditions
- **No medical advice:** Does not prescribe or recommend medications or treatments
- **No data sharing with third parties:** All conversation data stays on-device unless user explicitly exports
- **No content for minors:** Explicitly 18+ only (COPPA compliance)

---

## Functional Requirements

### FR-1: API Key Configuration
Users can securely configure their cloud LLM API key via Android Keystore. The key is stored encrypted and never transmitted off-device except for API calls.

### FR-2: Persona Selection
Users can choose between three AI personas (Therapist, Friend, Coach) each with distinct conversation styles and recovery approaches.

### FR-3: Chat Interface
Users can engage in real-time text conversations with the selected persona, with message history persisted locally.

### FR-4: Local Model Fallback
When no API key is configured or offline, the app uses an on-device LLM (via llama.cpp/MediaPipe) for basic conversational support.

### FR-5: CBT Exercises
Users can access guided Cognitive Behavioral Therapy exercises including thought journaling, cognitive restructuring, and behavioral activation activities.

### FR-6: Progress Analytics
Users can view visualizations of their emotional progress, streaks, and completed exercises over time.

### FR-7: Account Sync
Users can create an account to sync conversation history and progress across devices (end-to-end encrypted).

### FR-8: Export/Backup
Users can export their conversation history and progress data for personal backup or to transition to other tools.

### FR-9: Responsive UI
The app adapts seamlessly between phone and tablet layouts with appropriate multi-pane navigation on larger screens.

### FR-10: Privacy Controls
Users can delete all local data, clear conversation history, and review what data is stored at any time.

---

## Non-Functional Requirements

### Performance
- Cold start to chat screen: < 1500ms
- Message send to response display: < 2000ms (cloud) / < 5000ms (local model)
- Local model initialization: < 10s on mid-tier devices
- Database queries for chat history: < 100ms

### Security & Privacy
- API keys stored in Android Keystore with hardware-backed encryption
- All conversation data encrypted at rest using Android EncryptedSharedPreferences
- No network logging of conversation content in HTTP logs
- GDPR-compliant data deletion (full wipe within 48 hours)

### Accessibility
- WCAG 2.1 AA compliance
- TalkBack support for all interactive elements
- 48dp minimum touch targets
- Content descriptions for all icons
- High contrast mode support

### Reliability
- Offline-first: app fully functional without internet
- Graceful degradation: local model falls back automatically
- Crash rate < 0.5% monthly
- Data integrity: no conversation loss on app kill/restart

### Compatibility
- minSdk 26 (Android 8.0 Oreo)
- Tablet support: 7" and larger with optimized layouts
- Works on devices with as little as 2GB RAM (with model size caveats)

---

## Risks & Assumptions

- **R-1: Model size/performance trade-off:** On-device models are large (2-4GB) and slow. Assume users understand this limitation and accept longer response times.
- **A-1: Users will configure API keys:** Assume users are technically capable of obtaining and entering an API key. Fallback to local model mitigates if they don't.
- **R-2: Mental health liability:** App must include disclaimers that it's not therapy. Assume proper legal review of disclaimers.
- **A-2: CBT exercises are beneficial:** Assume evidence-based exercises (thought records, behavioral activation) provide measurable value. Will validate with user testing.
- **R-3: Account sync privacy:** End-to-end encryption adds complexity. Assume we can implement this without significant performance impact.
- **A-3: Tablet adoption:** Assume users will use on tablets, so responsive UI is worth the investment.

---

## Timeline

| Milestone | Date | Owner |
|-----------|------|-------|
| M1: Core chat + personas (phone) | Week 4 | Dev Team |
| M2: CBT exercises + basic analytics | Week 8 | Dev Team |
| M3: Tablet responsive UI | Week 10 | Dev Team |
| M4: Account sync | Week 12 | Dev Team |
| M5: Beta release (internal testing) | Week 14 | Release Engineer |
| M6: Public beta (open testing) | Week 16 | Release Engineer |

---

## User Stories

---

### US-1: API Key Configuration

**As a** user wanting cloud LLM quality responses  
**I want** to securely store my API key in the app  
**So that** I can access better conversational AI without the key leaving my device

**Priority:** Must  
**Estimate:** 8 story points

**Acceptance Criteria:**
- Given the user is on the API key setup screen, When they enter their key and tap "Save", Then the key is encrypted and stored in Android Keystore
- Given the app has a stored API key, When the user opens settings, Then they see "API Key: Configured" without the actual key visible
- Given the user wants to change/remove their key, When they tap "Change" in settings, Then they can enter a new key or clear the existing one
- Given no API key is configured, When the user starts a chat, Then the app uses the local model fallback

---

### US-2: Persona Selection

**As a** user seeking different types of support  
**I want** to choose between Therapist, Friend, and Coach personas  
**So that** I get conversational support tailored to my current needs

**Priority:** Must  
**Estimate:** 5 story points

**Acceptance Criteria:**
- Given the user opens the persona selector, When they see three options (Therapist, Friend, Coach), Then each option shows a description of the style
- Given the user selects a persona, When they start chatting, Then the assistant responds in that persona's style
- Given a persona is selected, When the user returns later, Then the app remembers their last-used persona
- Given the user is on a tablet, When they view the persona selector, Then they see a multi-pane layout with persona details

---

### US-3: Chat Conversation

**As a** user working through my emotions  
**I want** to have a text conversation with the AI  
**So that** I can process my feelings and receive guided support

**Priority:** Must  
**Estimate:** 13 story points

**Acceptance Criteria:**
- Given the user is in the chat screen, When they type a message and tap send, Then the message appears in their history immediately
- Given the user sends a message, When the system has an API key, Then the cloud LLM responds within 2 seconds (typically)
- Given the user sends a message, When offline but local model is available, Then the local model responds with appropriate quality
- Given the conversation is ongoing, When the user kills and restarts the app, Then all messages persist
- Given the user wants to start fresh, When they tap "New Conversation", Then previous messages are archived but accessible

---

### US-4: Local Model Fallback

**As a** user without an API key or internet connection  
**I want** the app to automatically use a local model  
**So that** I can still get support without configuration or connectivity

**Priority:** Must  
**Estimate:** 13 story points

**Acceptance Criteria:**
- Given no API key is configured, When the user opens chat, Then the local model loads and powers the conversation
- Given the device is offline, When the user tries to chat, Then the app silently switches to local model without error
- Given the local model is generating a response, When the user sees the UI, Then they see a subtle "offline mode" indicator
- Given the user is on a low-storage device, When they first use the app, Then they see a warning about model size requirements (2GB)

---

### US-5: CBT Thought Record

**As a** user working to reframe negative thinking  
**I want** to complete a guided thought record exercise  
**So that** I can practice identifying and challenging cognitive distortions

**Priority:** Must  
**Estimate:** 8 story points

**Acceptance Criteria:**
- Given the user opens CBT exercises, When they tap "Thought Record", Then they see a step-by-step form (Situation, Emotion, Automatic Thought, Evidence For/Against, Alternative Thought)
- Given the user completes the thought record, When they save it, Then it appears in their exercise history
- Given the user has saved thought records, When they view analytics, Then they see a count of completed exercises over time
- Given the user wants to review progress, When they view a saved exercise, Then they can see all responses and edit notes

---

### US-6: Behavioral Activation

**As a** user rebuilding my routine after a breakup  
**I want** to plan and track positive activities  
**So that** I can gradually restore structure and pleasure in my daily life

**Priority:** Should  
**Estimate:** 8 story points

**Acceptance Criteria:**
- Given the user opens CBT exercises, When they tap "Behavioral Activation", Then they can add activities with scheduled times
- Given the user has scheduled activities, When they mark an activity complete, Then it's recorded with a timestamp
- Given the user views their activity history, When they check analytics, Then they see completion rates and streaks
- Given the user wants encouragement, When they complete an activity, Then they receive a positive reinforcement message

---

### US-7: Progress Analytics

**As a** user tracking my recovery journey  
**I want** to see visual charts of my progress  
**So that** I can understand my improvement and stay motivated

**Priority:** Should  
**Estimate:** 8 story points

**Acceptance Criteria:**
- Given the user has completed exercises and chats, When they open analytics, Then they see a mood trend line over the past 30 days
- Given the user has used the app, When they view streaks, Then they see consecutive day counts with encouragement
- Given the user wants details, When they tap a chart element, Then they see specific entries for that time period
- Given the user is on a tablet, When they view analytics, Then they see expanded charts with detailed breakdowns

---

### US-8: Account Creation & Sync

**As a** user wanting my data across devices  
**I want** to create an account and sync my data  
**So that** I can seamlessly continue my recovery on any device

**Priority:** Could  
**Estimate:** 13 story points

**Acceptance Criteria:**
- Given the user taps "Sign In", When they create an account with email/password, Then their future data syncs across devices
- Given the user is signed in, When they complete an exercise on one device, Then it appears on their other devices within 30 seconds
- Given the user wants privacy, When they sign up, Then their data is end-to-end encrypted with their password (we don't have the key)
- Given the user deletes their account, When they confirm, Then all synced data is deleted from servers within 48 hours

---

### US-9: Tablet Responsive Layout

**As a** user on a tablet  
**I want** the interface to adapt to larger screens  
**So that** I have an optimized experience for the form factor

**Priority:** Must (for FULL tier)  
**Estimate:** 8 story points

**Acceptance Criteria:**
- Given the user is on a tablet, When they open the main screen, Then they see a two-pane layout (navigation list + content)
- Given the user is typing on a tablet, When the keyboard appears, Then the compose area remains visible and scrollable
- Given the user rotates their tablet, When the orientation changes, Then the layout adapts appropriately
- Given the user uses split-screen, When another app is visible, Then the UI remains usable with reduced width

---

### US-10: Privacy Dashboard

**As a** privacy-conscious user  
**I want** to see and control my data  
**So that** I maintain trust in the app's handling of sensitive information

**Priority:** Must  
**Estimate:** 5 story points

**Acceptance Criteria:**
- Given the user opens settings, When they view Privacy, Then they see what data is stored (conversations, exercises, account info)
- Given the user wants to clear data, When they tap "Delete All Conversations", Then the history is wiped and confirmed
- Given the user wants full reset, When they tap "Delete Account & Data", Then all local and remote data is removed
- Given the user reviews privacy, When they check "What We Collect", Then they see only essential analytics (crash reports, not conversation content)

---

### US-11: Crisis Resources

**As a** user potentially in distress  
**I want** quick access to crisis resources  
**So that** I can get human help when I need it

**Priority:** Must  
**Estimate:** 3 story points

**Acceptance Criteria:**
- Given the user is in chat or exercises, When they see concerning patterns, Then they see a non-intrusive "Need Help?" banner
- Given the user taps the crisis banner, When they open the resource screen, Then they see local/national hotlines and chat resources
- Given the user needs immediate help, When they tap a hotline, Then it dials immediately
- Given the user may not recognize crisis, When they use concerning keywords, Then the AI provides resources without pathologizing

---

## Prioritized Backlog (MoSCoW)

### Must (M1 - Core Value)
1. **US-10: Privacy Dashboard** (5 pts) - Foundation for trust
2. **US-1: API Key Configuration** (8 pts) - Enables cloud LLM
3. **US-4: Local Model Fallback** (13 pts) - Offline capability
4. **US-3: Chat Conversation** (13 pts) - Core experience
5. **US-2: Persona Selection** (5 pts) - Differentiation
6. **US-9: Tablet Responsive Layout** (8 pts) - Full tier requirement

### Should (M2 - Enhancement)
7. **US-5: CBT Thought Record** (8 pts) - Evidence-based exercises
8. **US-7: Progress Analytics** (8 pts) - Motivation through data
9. **US-6: Behavioral Activation** (8 pts) - Structured activities
10. **US-11: Crisis Resources** (3 pts) - Safety net

### Could (M3 - Polish)
11. **US-8: Account Creation & Sync** (13 pts) - Cross-device sync

---

## Roadmap

### Phase 1: MVP Foundation (Weeks 1-6)

**Sprint 1-2: Infrastructure & Privacy**
- Project setup (multi-module Clean Architecture)
- Privacy dashboard implementation
- Secure API key storage via Android Keystore
- Local model integration (llama.cpp/MediaPipe)

**Sprint 3-4: Core Chat**
- Chat screen UI (Compose)
- Persona selection logic
- Message persistence (Room)
- Local model fallback logic
- Tablet responsive layout

**Sprint 5-6: Testing & Polish**
- Unit tests for :core:domain (90% coverage target)
- UI tests for core screens
- Internal testing with small group
- Bug fixes and performance optimization

**Milestone M1: Core chat + personas (phone) - Week 6**

### Phase 2: Recovery Tools (Weeks 7-12)

**Sprint 7-8: CBT Exercises**
- Thought record exercise
- Behavioral activation exercise
- Exercise history and persistence
- UI for guided flows

**Sprint 9-10: Analytics & Visualization**
- Progress charts (mood trends, streaks)
- Data aggregation layer
- Tablet-optimized analytics view
- Export functionality

**Sprint 11-12: Integration**
- Combine chat context with exercises
- Suggested exercises based on chat patterns
- Performance tuning for tablet
- Beta preparation

**Milestone M2: CBT exercises + basic analytics - Week 12**

### Phase 3: Sync & Release (Weeks 13-16)

**Sprint 13-14: Account Sync**
- End-to-end encryption implementation
- Sync protocol design
- Cross-device conflict resolution
- Account UI flows

**Sprint 15-16: Beta Launch**
- Internal testing completion
- Play Store internal track release
- Monitoring and crash reporting
- Public beta (open testing track)

**Milestone M3: Beta release - Week 16**

---

## Definition of Ready

- [x] Written in the As a / I want / So that format
- All stories have acceptance criteria in Given/When/Then format
- Dependencies identified (Android Keystore, llama.cpp integration)
- Estimates provided
- Priority assigned using MoSCoW
- Tablet requirement noted for responsive UI

---

## Definition of Done

- [ ] All acceptance criteria pass (manual + automated)
- [ ] Code reviewed and merged (no critical/major issues)
- [ ] Unit tests written and passing (coverage targets met)
- [ ] UI tests written and passing (Roborazzi screenshots)
- [ ] Accessibility verified (TalkBack, 48dp targets)
- [ ] No new crashlytics issues in internal testing
- [ ] Documentation updated (README, inline comments)
- [ ] Released to internal testing track