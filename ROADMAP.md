# Pokedex-MAD — Learning roadmap

Build this app step-by-step using the reference:  
`D:\Study\Android\SkyDove\pokedex-compose-04-02-2026`

**How to use:** Work through phases in order. Check off steps as you complete them. This file will be updated incrementally as we add new steps.

---

## Phase 1: Foundation — Version catalog and repo setup

**Concept:** Centralize dependency versions in one place so every module stays consistent and upgrades are easy.

| Step | Task | Status |
|------|------|--------|
| 1.1 | ~~Fix compileSdk~~ Not needed: with **AGP 9**, `compileSdk { version = release(36) }` is valid (new block API). Use as-is. | ✅ N/A |
| 1.2 | Expand `gradle/libs.versions.toml`: add `[versions]` for ksp, hilt, androidxRoom, retrofit, okhttp, kotlinxSerialization, coroutines, androidxNavigation; add `[libraries]` using `version.ref`; add `[plugins]` for ksp, hilt, kotlinx-serialization. Use reference `libs.versions.toml` as guide. | ⬜ |
| 1.3 | (Optional) Set Java 17 in `app/build.gradle.kts`: `compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }`. | ⬜ |
| 1.4 | In `settings.gradle.kts`, add at top: `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")`. | ⬜ |

---

## Phase 2: First core module — `core:model`

**Concept:** `core:model` holds shared data classes (e.g. `Pokemon`). No Android UI, Room, or Retrofit—only Kotlin (and optional kotlinx.serialization). Other modules depend on it; it depends on no other app modules.

| Step | Task | Status |
|------|------|--------|
| 2.1 | Create folders: `core/model/`, `core/model/build.gradle.kts`, `core/model/src/main/kotlin/` + your package path (e.g. `ashraf/pokedex/mad/core/model/`). | ⬜ |
| 2.2 | In `settings.gradle.kts`, add: `include(":core:model")`. | ⬜ |
| 2.3 | In `core/model/build.gradle.kts`: apply Kotlin (and serialization if needed), set `namespace`, add only minimal deps (e.g. kotlinx.serialization). No Room/Retrofit/Compose. | ⬜ |
| 2.4 | Create one model class (e.g. `Pokemon.kt`) in the package with a few fields; add `@Serializable` if using serialization. | ⬜ |
| 2.5 | Sync Gradle. Optionally in `app`: `implementation(projects.core.model)` and use the model in a composable to verify. | ⬜ |

---

## Phase 3: Build logic (convention plugins)

*Steps will be added here when we start this phase.*

---

## Phase 4: More core modules (network, database, data)

*Steps will be added here when we start this phase.*

---

## Phase 5: Navigation and feature modules

*Steps will be added here when we start this phase.*

---

## Phase 6: Testing

*Steps will be added here when we start this phase.*

---

## Phase 7: Code quality (Spotless)

*Steps will be added here when we start this phase.*

---

## Phase 8: CI/CD (GitHub Actions)

*Steps will be added here when we start this phase.*

---

## Phase 9: Baseline profiles

*Steps will be added here when we start this phase.*

---

*Last updated: roadmap created; Phases 1–2 defined.*
