# Pokedex-MAD — Learning roadmap

Build this app step-by-step using the reference:  
`D:\Study\Android\SkyDove\pokedex-compose-04-02-2026`

**How to use:** Work through phases in order. Check off steps as you complete them. This file will be updated incrementally as we add new steps.

---

## Phase 1: Foundation — Version catalog and repo setup

**Concept:** Centralize dependency versions in one place so every module stays consistent and upgrades are easy.

| Step | Task | Status |
|------|------|--------|
| 1.1  | Expand `gradle/libs.versions.toml`: add `[versions]` for ksp, hilt, androidxRoom, retrofit, okhttp, kotlinxSerialization, coroutines, androidxNavigation; add `[libraries]` using `version.ref`; add `[plugins]` for ksp, hilt, kotlinx-serialization. Use reference `libs.versions.toml` as guide. | ✅ |
| 1.2  | (Optional) Set Java 17 in `app/build.gradle.kts`. | ✅ |
| 1.3  | In `settings.gradle.kts`, add at top: `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")`. | ✅ |

### Plugins in the catalog (reference vs you)

In the reference there are many entries under `[plugins]`. You don’t need all of them at once:

| Plugin | Reference has it? | You need it when |
|--------|--------------------|------------------|
| `android-application` | ✅ | Already have (app module). |
| `kotlin-compose` | as `compose-compiler` | Already have (Compose in app). |
| `android-library` | ✅ | **Phase 2:** when you add `core:model`, `core:network`, etc. Add to catalog and use in each library module. |
| `kotlin-android` | ✅ | **Phase 2:** use with `android-library` in every Android library module (e.g. `core:model`). Add to catalog. |
| `kotlin-jvm` | ✅ | Only if you add a **JVM-only** module (no Android), e.g. like reference’s `core:common`. Skip until then. |
| `kotlinx-serialization` | ✅ | You have it; use in `core:model` when you add `@Serializable` models. |
| `ksp` | ✅ | You have it; needed for Room, Hilt, etc. |
| `hilt-plugin` | ✅ | You have it. |
| `kotlin-parcelize` | ✅ | Optional; add when you need `Parcelable` (e.g. passing objects in intents). |
| `android-test`, `spotless`, `baselineprofile`, `protobuf-plugin` | ✅ | Add in later phases (baseline profile, Spotless, DataStore/Protobuf). |

**For Phase 2:** add to `[plugins]` in `libs.versions.toml`:

- `android-library = { id = "com.android.library", version.ref = "agp" }`
- `kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }`

(And in root `build.gradle.kts` add `alias(libs.plugins.android.library) apply false` and `alias(libs.plugins.kotlin.android) apply false` so modules can apply them.)

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

*Last updated: Phase 1 verified; plugin table and Phase 2 plugin steps added.*
