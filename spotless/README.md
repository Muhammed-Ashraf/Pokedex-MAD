# Spotless license header files

These files are used by the **Spotless convention plugin** (`ashraf.pokedex.mad.spotless`) to add a consistent license header to source files.

## What each file is for

| File | Used for | How it's applied |
|------|----------|------------------|
| **spotless.license.kt** | Kotlin (`.kt`) and Gradle Kotlin DSL (`.kts`) files | The plugin prepends this block comment to every matching file that doesn't already have it. Content is the Apache 2.0 license header. |
| **spotless.license.xml** | XML files (e.g. `AndroidManifest.xml`, layout XML) | The plugin prepends this XML comment block before the first tag in each file. |

## Why they live at the project root

The convention plugin runs in each module (app, core:model, core:network, etc.) but configures Spotless to read these files from the **root project**: `rootProject.file("spotless/spotless.license.kt")`. So there is a single source of truth for the license text; changing it here updates what gets applied in all modules.

## Concepts

- **License header:** Many projects require a short copyright/license block at the top of every source file. Spotless can add or enforce it automatically.
- **Convention plugin:** Instead of every module defining its own `spotless { }` block and path to these files, one convention plugin applies Spotless with this configuration everywhere. See `build-logic/convention/src/main/kotlin/ashraf/pokedex/mad/SpotlessConventionPlugin.kt`.

## Commands

- `./gradlew spotlessApply` — Fix formatting and add missing headers in the whole project.
- `./gradlew spotlessCheck` — Fail the build if any file is not formatted (e.g. in CI).
