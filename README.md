## Pokedex-MAD (Work in Progress)

Portfolio / learning Android project built step-by-step, inspired by the reference project
[skydoves/pokedex-compose](https://github.com/skydoves/pokedex-compose).

### Goals
- Practice modern Android development (MAD): modularization, DI, network + cache, clean layering.
- Keep the build scalable using Gradle Version Catalog + convention plugins (`build-logic/`).

### Tech stack (so far)
- Kotlin
- Jetpack Compose
- Hilt (DI)
- Retrofit + OkHttp (network)
- Room (database)
- Coroutines / Flow
- Sandwich (API response wrapper)
- Spotless (formatting)

### Modules
- `app/` — app entry point + UI
- `core:model` — domain models
- `core:network` — Retrofit service + network DI
- `core:database` — Room DB + DAO + entity↔domain mappers
- `core:data` — repositories combining network + database (in progress)
- `build-logic/` — convention plugins to keep module Gradle files minimal

### Progress / roadmap
See `ROADMAP.md` for the step-by-step plan and what’s completed.

### How to run
- Open in Android Studio
- Sync Gradle
- Run the `app` configuration on an emulator/device
