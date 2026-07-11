# Project Context

> Shared source of truth for the Android Development Suite. Every skill reads
> this file. Copy it to your repo root as `PROJECT_CONTEXT.md` and keep it
> updated as ADRs change decisions. Do NOT let skills invent conventions that
> contradict this document.

## Product
- App name: Breakup Recovery Chatbot
- One-line purpose: Help users process a breakup and rebuild emotional resilience via AI-guided conversations.
- Target audience: Adults (18+) experiencing romantic breakup, seeking structured support.
- Platforms: Phone, tablet — minSdk 26
- Distribution: Play Store open testing → production (feature flags for AI backend)

## Tech Stack (decided — do not relitigate without an ADR)
- Language: Kotlin (2.x)
- UI: Jetpack Compose (Material3)
- Architecture: MVVM + Clean Architecture (multi-module)
- DI: Hilt
- Local data: Room
- Remote data: Retrofit + OkHttp (kotlinx.serialization)
- Async: Kotlin Coroutines + Flow
- Images: Coil
- Navigation: Compose Navigation (type-safe args)
- Maps/graph: [library]

## Conventions
- Package: `com.eallora.breakupchatbot`
- Module layout: `:app`, `:core:ui`, `:core:common`, `:core:domain`,
  `:core:data`, `:feature:*`
- Naming: Feature = PascalCase; files `<Feature>ViewModel.kt`,
  `<Feature>Screen.kt`, `<Feature>UiState.kt`, `<Feature>Repository.kt`.
- UiState: one sealed interface per screen (Loading/Error/Success).
- Strings: all in `res/values/strings.xml` (no hardcoded text).
- Tests: JUnit + Truth + MockK + Turbine; Compose UI via
  `createAndroidComposeRule` / Roborazzi.

## Architecture Rules (enforced by android-review-skill)
- UI never imports Retrofit/Room directly.
- `:core:domain` has zero Android dependencies.
- `feature:*` depends only on `:core:domain` + `:core:ui`.
- Single source of truth: network writes update the DB; UI observes the DB.

## CI / Quality Gates
- Lint + Detekt + Ktlint must be clean to merge.
- Minimum coverage: `:core:domain` 90%, `:core:data` 80%, `:feature:*` 70%.
- Merge only after Review approves (no critical/major) and tests pass.

## Release
- Signing: Play App Signing (upload key in CI secrets).
- Versioning: `versionCode = MAJOR*10000 + MINOR*100 + PATCH`.
- Rollout: internal → closed → production 10% → 50% → 100%.

## Open Decisions / ADRs
- [ADR-1: AI backend routing — cloud LLM via user API key (Android Keystore) + on-device local model (llama.cpp) fallback; secure key storage, offline-first, no backend]
