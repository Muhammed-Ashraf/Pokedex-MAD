# Testing plan (Pokedex-MAD)

Simplified plan: **mirror the reference** (`pokedex-compose`) where it matches solid practice. Optional items are **extra**, not required for parity.

Work through this with step-by-step guidance (Phase A → B → …).

---

## Mirror (same idea as reference)

| Step | What |
|------|-----|
| **A** | **`:core:test`** module — `MainCoroutinesRule`, small helpers (e.g. `MockUtil`). `include` in `settings.gradle.kts`. |
| **B1** | **`:core:datastore`** — `src/test`: serializer test, `PreferencesDataSource` test, in-memory / fake `DataStore` helper. |
| **B2** | **`:core:database`** — `src/test`: DAO test + in-memory DB helper. |
| **B3** | **`:core:network`** — `src/test`: API/service tests + shared test base (e.g. mock web server). |
| **B4** | **`:core:data`** — `src/test`: `UserDataRepository`, `HomeRepositoryImpl`, `DetailsRepository` tests (use `core:test` + fakes / in-memory). |
| **B5** | **Gradle** — per module: `testImplementation` for JUnit, coroutines-test, `projects.core.test`, and whatever each module needs (Room test, OkHttp mock, etc.). |

Run: `./gradlew test` after each chunk.

---

## Optional (reference does not rely on these)

- ViewModel unit tests (`SettingsViewModel`, …).
- Compose UI tests (`createComposeRule`, …).
- `src/androidTest` (Espresso / Hilt) — reference lists deps on `app` but has no real `androidTest` sources in that layout.
- Turbine / extra assertion libs — taste only.

---

## Later (roadmap)

- **CI** (Phase 8): `test` + `spotlessCheck` on PRs.
- **Baseline profiles** (Phase 9): separate from this unit-test plan.

---

## Order to implement

1. **A** — `core:test`  
2. **B1** → **B2** → **B3** → **B4** (or B3 before B2 if you prefer network first)

---

*Check off steps as you finish; adjust file names to match your packages (`ashraf.pokedex.mad`).*
