# Testing plan (Pokedex-MAD)

Simplified plan: **mirror the reference** (`pokedex-compose`) where it matches solid practice. This file tracks **what is done** and **what is explicitly out of scope** for now.

---

## Current status (unit-test baseline — **complete**)

| Area | Module / location | Notes |
|------|-------------------|--------|
| Test helpers | `:core:test` | `MainCoroutinesRule`, `MockUtil`, etc. |
| Datastore | `:core:datastore` | Serializer + `PreferencesDataSource` tests |
| Database | `:core:database` | DAO + in-memory DB tests |
| Network | `:core:network` | API / service tests |
| Repositories | `:core:data` | `UserDataRepository`, `HomeRepositoryImpl`, `DetailsRepository` tests |
| ViewModels | `:feature:home`, `:feature:settings` | `HomeViewModel`, `DetailsViewModel`, `SettingsViewModel` tests |


## Original mirror checklist (reference-aligned)

| Step | What |
|------|------|
| **A** | **`:core:test`** — `MainCoroutinesRule`, small helpers (e.g. `MockUtil`). |
| **B1** | **`:core:datastore`** — `src/test`: serializer, `PreferencesDataSource`, in-memory `DataStore` helper. |
| **B2** | **`:core:database`** — `src/test`: DAO test + in-memory DB helper. |
| **B3** | **`:core:network`** — `src/test`: API/service tests + shared test base (e.g. mock web server). |
| **B4** | **`:core:data`** — `src/test`: repository tests (`UserDataRepository`, `HomeRepositoryImpl`, `DetailsRepository`) using `core:test` + mocks / in-memory. |
| **B5** | **Gradle** — per module: `testImplementation` for JUnit, coroutines-test, `projects.core.test`, and extras each module needs. |


## Later (roadmap)

- **CI** (Phase 8): `test` + `spotlessCheck` on PRs.
- **Baseline profiles** (Phase 9): separate from this unit-test plan.

---

