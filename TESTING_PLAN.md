# Testing plan (Pokedex-MAD)

This document describes **what is tested in this repo**, **how to run tests**, and what is **intentionally out of scope** for now.

---

## Unit test coverage

| Area | Module / location | Notes |
|------|-------------------|--------|
| Test helpers | `:core:test` | `MainCoroutinesRule`, `MockUtil`, etc. |
| Datastore | `:core:datastore` | Serializer + `PreferencesDataSource` tests |
| Database | `:core:database` | DAO + in-memory DB tests |
| Network | `:core:network` | API / service tests |
| Repositories | `:core:data` | `UserDataRepository`, `HomeRepositoryImpl`, `DetailsRepository` tests |
| ViewModels | `:feature:home`, `:feature:settings` | `HomeViewModel`, `DetailsViewModel`, `SettingsViewModel` tests |

Per-feature Gradle: `testImplementation` for JUnit, `kotlinx-coroutines-test`, and `projects.core.test` where needed.

---

## Commands

**All unit tests (Android library modules):**

```bash
./gradlew testDebugUnitTest
```

**Shortcut used in CI and locally:**

```bash
./gradlew test
```

**Single module example:**

```bash
./gradlew :core:data:testDebugUnitTest
```

**Formatting (often run with tests):**

```bash
./gradlew spotlessCheck
```

Fix formatting:

```bash
./gradlew spotlessApply
# or per module, e.g.:
./gradlew :core:preview:spotlessApply
```

---

## What each layer tests

| Layer | Typical focus |
|-------|----------------|
| **Datastore** | Proto round-trip, default values, corruption handling |
| **Database** | DAO queries against in-memory Room |
| **Network** | Retrofit service + Sandwich `ApiResponse` parsing (mock server / fakes) |
| **Repositories** | Offline-first flows, error paths, mapping entity ↔ domain |
| **ViewModels** | State after repository callbacks; uses test scope + stub repositories |

---

## Out of scope

These are not part of this repo’s test suite today; you can add them in a fork:

- **Full-app `androidTest`** (e.g. `MainActivity` + Compose rule smoke)
- **Compose UI tests** on device (isolated `setContent { }` + fakes is a good first step)
- **Navigation E2E** on device (JVM navigator tests are a lighter alternative)

Optional polish later: Turbine for tricky flows; Paparazzi for JVM screenshot-style composable tests.

---

## CI

`.github/workflows/android.yml` runs `spotlessCheck`, `testDebugUnitTest`, and `:app:assembleDebug` on pushes/PRs to `master`.

---

## Adding a new test

1. Put tests under `src/test/kotlin/...` using the same package as production code.
2. Reuse `:core:test` for coroutine rules and shared fakes.
3. For DataStore/Room, prefer **in-memory** implementations (see existing tests).
4. Run module-scoped `testDebugUnitTest` before opening a PR.
