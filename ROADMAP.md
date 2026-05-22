# Pokedex-MAD — Learning roadmap

Use this repo as a **self-contained** MAD sample: build the same ideas yourself, or read the code organized by phase below.

**Note:** The design system lives in **`core:designsystem`** (shared across features), not a `feature:designsystem` module.

---

## Suggested reading order (after clone)

1. **Gradle shape** — `settings.gradle.kts`, root `build.gradle.kts`, `gradle/libs.versions.toml`, `build-logic/convention/`
2. **Domain** — `core/model`
3. **Data** — `core/network` → `core/database` → `core/common` → `core/data` → `core/datastore`
4. **UI shell** — `core/designsystem` → `core/navigation` → `core/preview`
5. **Features** — `feature/home` (list + details), `feature/settings`
6. **App** — `app/` (`PokedexApp`, `MainActivity`, `PokedexNavHost`)
7. **Performance** — `baselineprofile/`, `app/src/main/baseline-prof.txt` (if present)

---

## Architecture (one screen)

```text
┌─────────────────────────────────────────────────────────┐
│  :app  — Activity, NavHost, theme from UserData         │
└───────────────────────────┬─────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
  :feature:home      :feature:settings    :core:designsystem
  ViewModels + UI    Settings VM + UI     Theme, components
        │                   │
        └─────────┬─────────┘
                  ▼
            :core:data  (repository interfaces + impl)
                  │
     ┌────────────┼────────────┐
     ▼            ▼            ▼
:core:network :core:database :core:datastore
 PokeAPI       Room cache     theme prefs (proto)
```

**Rule of thumb:** Composables and ViewModels depend on **repository interfaces** in `core:data`, not on Retrofit or Room directly.

---

## Phase 1 — Version catalog

**Goal:** One place for dependency versions.

| Learn | Where |
|-------|--------|
| `[versions]`, `[libraries]`, `[plugins]` | `gradle/libs.versions.toml` |
| Type-safe accessors | `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")` in `settings.gradle.kts` |

**Exercise:** Bump a library version in the catalog and sync; see all modules pick it up.

---

## Phase 2 — Convention plugins (`build-logic`)

**Goal:** Repeatable Android/Kotlin/Compose/Hilt/Spotless config per module type.

| Plugin id (examples) | Applies to |
|----------------------|------------|
| `ashraf.pokedex.mad.android.library` | Most `core:*` libraries |
| `ashraf.pokedex.mad.android.hilt` | Modules needing DI |
| `ashraf.pokedex.mad.android.feature` | `feature:*` (designsystem + navigation + data deps) |
| `ashraf.pokedex.mad.android.application` | `:app` |

**Read:** `build-logic/convention/src/main/kotlin/Android*ConventionPlugin.kt`, `KotlinAndroid.kt`, `configureAndroidCompose.kt`.

**Why it matters:** New modules stay small — `namespace` + `dependencies` only.

---

## Phase 3 — `core:model`

**Goal:** Serializable domain types shared everywhere.

- `Pokemon`, `PokemonInfo`, `UserData`, `UiTheme`, etc.
- `@Serializable` for network JSON where needed.

---

## Phase 4 — Data layer

### 4.1 `core:network`

- Retrofit `PokedexService`, DTOs, Sandwich `ApiResponse`
- `PokedexClient` facade (single entry for repositories)
- `NetworkModule` (Hilt)

### 4.2 `core:database`

- `PokemonEntity`, `PokemonInfo` entities, DAOs, `PokedexDatabase`
- Entity ↔ domain mappers (keeps Room types out of `core:model`)

### 4.3 `core:common` (JVM)

- `@Dispatcher(IO)`, app-wide `CoroutineScope` for long-running work

### 4.4 `core:data`

- `HomeRepository`, `DetailsRepository`, `UserDataRepository` + implementations
- **Offline-first home:** read DB → if empty, fetch API → cache → emit
- **Fakes** (`FakeHomeRepository`, `FakeDetailsRepository`) for previews/tests

---

## Phase 5 — UI, navigation, features

### Design system (`core:designsystem`)

- `PokedexTheme`, colors (XML + Compose), shared components (`PokedexText`, progress, app bar)
- Re-exports Landscapist APIs used by features

### Navigation (`core:navigation`)

- `PokedexScreen`, navigator, Compose `LocalComposeNavigator`
- Navigation 3 style host in `app`

### Previews (`core:preview`)

- `PreviewUtils` — mock Pokémon list/detail for `@Preview`

### `feature:home`

- `HomeViewModel` — pagination via `HomeRepository`
- `DetailsViewModel` — assisted inject with selected `Pokemon`
- `PokedexHome`, `PokedexDetails` + shared transition details UI

### `feature:settings` + `core:datastore`

- Proto `UserPreferences`, `PreferencesDataSource`
- Theme selection → `MainActivity` / `PokedexTheme` reads `UserData`

### ViewModels

- Extend `androidx.lifecycle.ViewModel`
- UI state via `StateFlow` with explicit backing `MutableStateFlow` (Kotlin 2.3 style)

---

## Phase 6 — Testing

See **`TESTING_PLAN.md`**. Focus: repositories and ViewModels with fakes + coroutine test scope.

---

## Phase 7 — Spotless

- Applied via convention plugin per module
- Run `spotlessCheck` before commit; `spotlessApply` to fix

---

## Phase 8 — CI & release

- **GitHub Actions** — `spotlessCheck`, `testDebugUnitTest`, `:app:assembleDebug` (see `.github/workflows/android.yml`)
- **R8** — `proguard-rules.pro` for release builds
- **Release signing** — keys in root `local.properties` (gitignored) for local release builds
- **Optional fork idea** — run `assembleRelease` in CI with GitHub secrets if you publish to Play

---

## Phase 9 — Baseline profiles

- `:baselineprofile` generates startup profiles for `:app`
- Artifacts committed; regeneration on a **connected device** when startup paths change
- Not regenerated in CI (uses committed `baseline-prof.txt` / `startup-prof.txt`)

---

## Common Gradle commands

```bash
./gradlew :app:assembleDebug
./gradlew spotlessCheck testDebugUnitTest
./gradlew :core:data:testDebugUnitTest
```

---

## Ideas for your own fork

- Add search or favorites (new repository + Room table)
- Type-safe Navigation arguments
- `assembleRelease` in CI with GitHub secrets
- Instrumented smoke test for `MainActivity`
- Remove optional tooling you do not use (e.g. stability analyzer, HotSwan if added)
