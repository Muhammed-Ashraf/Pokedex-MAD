# Pokedex-MAD

A **modular Android** Pokédex app built to learn **Modern Android Development (MAD)** step by step: Gradle convention plugins, layered architecture, Jetpack Compose, Hilt, Room, Retrofit, Proto DataStore, and baseline profiles.

This repository is meant to be **read and built on its own** — no external sample project required.

---

## What you will learn

- **Multi-module Gradle** with a version catalog (`gradle/libs.versions.toml`) and `build-logic/` convention plugins
- **Clean-ish layers**: UI → ViewModel → repository → network / database / DataStore
- **Offline-first** home list (Room cache + PokeAPI pagination)
- **Compose** UI with a shared design system, Navigation 3, and `@Preview` helpers
- **Settings** persisted with **Proto DataStore** (theme preference)
- **Unit tests** on repositories, DAOs, and ViewModels
- **CI**, R8 release config, and **baseline profiles** for startup

---

## Requirements

- Android Studio (recent stable)
- JDK 17
- Android SDK 36 (compileSdk in project)
- Emulator or device (API 24+; baseline profile generation needs API 29+ test module / connected device)

---

## Quick start

```bash
git clone <your-repo-url>
cd Pokedex-MAD
./gradlew :app:assembleDebug
```

Open the project in Android Studio, sync Gradle, run the **app** configuration.

**Checks before a PR or share:**

```bash
./gradlew spotlessCheck testDebugUnitTest :app:assembleDebug
```

Release build (optional; needs signing keys in `local.properties`):

```bash
./gradlew :app:assembleRelease
```

---

## Module map

| Module | Role |
|--------|------|
| `:app` | `Application`, `MainActivity`, navigation host, theme wiring |
| `:core:model` | Domain models (`Pokemon`, `PokemonInfo`, `UserData`, …) |
| `:core:network` | Retrofit + PokeAPI, Sandwich `ApiResponse`, `PokedexClient` |
| `:core:database` | Room entities, DAOs, DB migrations |
| `:core:common` | JVM dispatchers + app `CoroutineScope` (Hilt) |
| `:core:data` | Repository implementations + fakes for previews/tests |
| `:core:datastore` | Proto **UserPreferences**, theme storage |
| `:core:designsystem` | `PokedexTheme`, colors, shared Compose components |
| `:core:navigation` | Routes, navigator, Compose integration |
| `:core:preview` | `PreviewUtils` + preview theme wrapper |
| `:core:test` | `MainCoroutinesRule`, test helpers |
| `:feature:home` | Home list + Pokémon details UI & ViewModels |
| `:feature:settings` | Settings dialog & theme selection |
| `:baselineprofile` | Profile generator + startup benchmarks |
| `build-logic` | Convention plugins (`android.library`, `android.hilt`, …) |

**Data flow (home list):**  
`PokedexHome` → `HomeViewModel` → `HomeRepository` → `PokemonDao` / `PokedexClient` → PokeAPI

---

## How to study this repo

1. Read **`ROADMAP.md`** — phases, what each module teaches, suggested reading order.
2. Read **`TESTING_PLAN.md`** — what is tested and how to run tests.
3. Trace one feature end-to-end (recommended: **home list**):
   - `feature/home/.../PokedexHome.kt`
   - `HomeViewModel.kt` → `HomeRepositoryImpl.kt` → `PokemonDao.kt` / network layer
4. Trace **theme/settings**: `SettingsViewModel` → `UserDataRepository` → `PreferencesDataSource` → proto files in `core/datastore`.

Comments in source explain **what / why / when** for non-obvious choices (pagination, assisted inject for details, etc.).

---

## Tech stack

- Kotlin 2.3, AGP 9, Gradle 9
- Jetpack Compose, Material 3, Navigation 3
- Hilt, KSP, Room, Retrofit, OkHttp, kotlinx.serialization
- Sandwich, Landscapist, Proto DataStore
- Spotless, Compose stability analyzer (debug)
- GitHub Actions: `spotlessCheck`, unit tests, debug assemble

---

## Docs

| File | Purpose |
|------|---------|
| [ROADMAP.md](ROADMAP.md) | Learning path, module phases, architecture notes |
| [TESTING_PLAN.md](TESTING_PLAN.md) | Unit-test coverage and commands |

---

## License

Apache License 2.0 — see [LICENSE](LICENSE) if present, or per-file headers in `src/`.

Third-party libraries (Hilt, Landscapist, Sandwich, etc.) are used under their respective licenses.
