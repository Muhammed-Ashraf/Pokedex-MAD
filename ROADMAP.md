# Pokedex-MAD — Learning roadmap

Build this app step-by-step using the reference:  
`D:\Study\Android\SkyDove\pokedex-compose-04-02-2026`

**How to use:** Phases **1 → 6** are at a **completed baseline** for this repo (see snapshot below). **Next roadmap work:** **Phase 8** (CI + release hardening), then **Phase 9** (baseline profiles). Older step tables below still teach the journey; **✅ / ⬜** in summary rows is kept in sync with what exists in git.

---

## Status snapshot (current repo)

| Phase | State | Notes |
|-------|--------|--------|
| **1–2** | ✅ | Version catalog, `build-logic`, Android library / Hilt / Spotless / feature / application convention plugins. |
| **3** | ✅ | `core:model` + serialization. |
| **4** | ✅ | `core:network`, `core:database`, `core:common`, `core:data`, `PokedexClient`, repositories, Hilt modules; app convention plugins on `:app`. |
| **5** | ✅ | `core:designsystem` (theme + **Landscapist** re-exports), `core:navigation`, `core:viewmodel`, `core:preview`, **`feature:home`**, **`feature:settings`**, Navigation 3 + `PokedexNavHost`, home/details UI, **`core:datastore`** + settings/theme. |
| **6** | ✅ | Unit tests across `core:*` and feature ViewModels; details in **`TESTING_PLAN.md`**. Full-app **`androidTest` / Compose UI smoke** intentionally **out of scope** for now. |
| **7** | ✅ | Spotless via convention plugin; run `spotlessCheck` locally / in CI. |
| **8** | ⬜ **Next** | GitHub Actions (or CI): `test`, `spotlessCheck`, `assembleDebug`; later release signing + R8 (see Phase 8). |
| **9** | ⬜ | Baseline profiles + optional macrobenchmark (when you want startup work). |

**Naming note:** The roadmap originally said **`feature:designsystem`**; this project uses **`core:designsystem`** (shared UI tokens + Landscapist APIs) — same role for theming and previews.

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
| `android-library` | ✅ | Used inside convention plugin (Phase 2); library modules apply the plugin. |
| `kotlin-android` | ✅ | Used inside convention plugin (Phase 2); library modules apply the plugin. |
| `kotlin-jvm` | ✅ | Only if you add a **JVM-only** module (no Android), e.g. like reference’s `core:common`. Skip until then. |
| `kotlinx-serialization` | ✅ | You have it; use in `core:model` when you add `@Serializable` models. |
| `ksp` | ✅ | You have it; needed for Room, Hilt, etc. |
| `hilt-plugin` | ✅ | You have it. |
| `kotlin-parcelize` | ✅ | Optional; add when you need `Parcelable` (e.g. passing objects in intents). |
| `android-test`, `spotless`, `baselineprofile`, `protobuf-plugin` | ✅ | Add in later phases (baseline profile, Spotless, DataStore/Protobuf). |

---

## Phase 2: Build logic (convention plugins)

**Concept:** Convention plugins live in an included build (`build-logic`) and apply the same Android/Kotlin config to every library module. So each module’s `build.gradle.kts` stays minimal (like the reference): only `namespace` and dependencies. Do this **before** adding `core:model` so new modules are minimal from the start.

| Step | Task | Status |
|------|------|--------|
| 2.1 | Create `build-logic/` at project root. Add `build-logic/settings.gradle.kts` that includes a subproject (e.g. `convention`). Add `build-logic/convention/build.gradle.kts` with `kotlin-dsl`, Java 17, and `compileOnly` for AGP, Kotlin, Compose compiler (from root libs). | ✅ |
| 2.2 | In root `settings.gradle.kts`, add `pluginManagement { includeBuild("build-logic") }` so the root build can apply your convention plugin ids. | ✅ |
| 2.3 | In `build-logic/convention/`, create shared Kotlin config: e.g. `KotlinAndroid.kt` with `configureKotlinAndroid(CommonExtension)` (compileSdk, minSdk, Java 17, compileOptions) and `configureKotlinAndroid(KotlinAndroidProjectExtension)` (jvmTarget, freeCompilerArgs). | ✅ |
| 2.4 | Create **Android library convention plugin**: applies `com.android.library` and `org.jetbrains.kotlin.android`, calls `configureKotlinAndroid` for both extensions. Register in `build-logic/convention/build.gradle.kts` with an id (e.g. `yourproject.android.library`). | ✅ |
| 2.5 | Sync. Verify: root build can resolve the plugin (no need to use it in a module until Phase 3). | ✅ |

**Reference:** `build-logic/convention/build.gradle.kts`, `AndroidLibraryConventionPlugin.kt`, `KotlinAndroid.kt`, root `settings.gradle.kts` (includeBuild).

---

### Phase 2 — Step-by-step with explanations

**Prerequisite:** So the build-logic project can compile plugin code that uses Android/Kotlin Gradle APIs, add these to the **root** `gradle/libs.versions.toml` under `[libraries]` (they are not applied to your app; they are only used by the convention build):

- `android-gradlePlugin = { group = "com.android.tools.build", name = "gradle", version.ref = "agp" }`
- `kotlin-gradlePlugin = { group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version.ref = "kotlin" }`

---

#### Step 2.1 — Create the `build-logic` structure and convention project

**What you’re doing:** You’re adding a separate Gradle build named `build-logic` that the root build will “include.” That included build has one subproject, `convention`, where we write the convention plugins in Kotlin. The root project does **not** put plugin code in `buildSrc` or the app; it lives here so it’s reusable and cacheable.

**What to do:**

1. **Create the folder** `build-logic/` at the project root (same level as `app/`).

2. **Create `build-logic/settings.gradle.kts`** with:
   - `dependencyResolutionManagement { repositories { google(); mavenCentral() } }` so the convention build can resolve dependencies.
   - A **version catalog** so the convention build can use the same `libs` as the root project (e.g. `libs.android.gradlePlugin`). Use:
     - `versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }`
     - The path `../gradle/libs.versions.toml` is relative to the `build-logic` folder, so it points to the root project’s version catalog.
   - `include(":convention")` so Gradle treats `build-logic/convention` as a subproject.

   Reference: `D:\Study\Android\SkyDove\pokedex-compose-04-02-2026\build-logic\settings.gradle.kts` (you can skip `enableFeaturePreview` there if the root already has it).

3. **Create the folder** `build-logic/convention/` and inside it **`build-logic/convention/build.gradle.kts`** with:
   - `plugins { \`kotlin-dsl\` }` — enables writing build logic in Kotlin (`.kt` files in `src/main/kotlin`).
   - `group = "ashraf.pokedex.mad.buildlogic"` (or any stable group you like).
   - `java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }` — the convention build compiles with Java 17.
   - `dependencies { compileOnly(libs.android.gradlePlugin); compileOnly(libs.kotlin.gradlePlugin) }` — so the plugin code can reference `LibraryExtension`, `KotlinAndroidProjectExtension`, etc. without packaging those into the plugin.
   - **Do not** add the `gradlePlugin { plugins { ... } }` block yet; that comes in step 2.4.

**Why:** The convention build is compiled and run by Gradle when you sync or build. It must see the same AGP and Kotlin versions as the rest of the project and use the root version catalog so one place controls versions.

---

#### Step 2.2 — Wire the included build into the root project

**What you’re doing:** Telling the root project that there is another Gradle build in `build-logic` that provides plugins. Once this is in place, any module in the root (or in included builds) can apply a plugin by id (e.g. `id("ashraf.pokedex.mad.android.library")`) and Gradle will run the convention build to get that plugin.

**What to do:**

- Open the **root** `settings.gradle.kts` (the one next to `app/`).
- Inside **`pluginManagement { }`**, at the top of the block (e.g. right after the opening brace), add:
  - **`includeBuild("build-logic")`**

So the root finds plugins from the root’s `pluginManagement` repos **and** from the `build-logic` build (which will register its plugins in step 2.4).

**Why:** `includeBuild` makes the plugins registered in `build-logic` visible to the root and all its subprojects. Without it, `id("ashraf.pokedex.mad.android.library")` would not be found.

---

#### Step 2.3 — Shared Kotlin/Android config (used by the library plugin)

**What you’re doing:** The convention plugin will apply the same `compileSdk`, `minSdk`, Java 17, and Kotlin options to every Android library. To avoid duplicating that logic in every plugin class, we put it in a shared function (or two) in a Kotlin file. The **Android library** convention plugin (step 2.4) will call these functions.

**What to do:**

1. Create the source folder for the convention project: **`build-logic/convention/src/main/kotlin/`**. You can use a package like **`ashraf.pokedex.mad`** (e.g. folder `ashraf/pokedex/mad/` under `kotlin/`).

2. In that package, create a file **`KotlinAndroid.kt`** (or similar) with two functions:

   **First function — config for the Android block (compileSdk, minSdk, Java 17):**

   - Name: e.g. `fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>)`.
   - Parameter type: `com.android.build.api.dsl.CommonExtension` (from AGP; use the full type or import).
   - Inside: on `commonExtension` set:
     - `compileSdk = 36`
     - `defaultConfig { minSdk = 29 }` (or the value you use in the app)
     - `compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }`
     - Optionally `lint { abortOnError = false }`
   - Mark the function `internal` so it’s only used inside the convention project.

   **Second function — config for the Kotlin block (JVM target, compiler args):**

   - Name: e.g. `fun Project.configureKotlinAndroid(extension: KotlinAndroidProjectExtension)`.
   - Parameter type: `org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension`.
   - Inside: on `extension` set:
     - `compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }`
     - Optionally `freeCompilerArgs` with opt-ins you need (e.g. `kotlin.RequiresOptIn`, `kotlinx.coroutines.ExperimentalCoroutinesApi`). You can start minimal and add later.

   Reference: `D:\Study\Android\SkyDove\pokedex-compose-04-02-2026\build-logic\convention\src\main\kotlin\com\skydoves\pokedex\compose\KotlinAndroid.kt`. Use the same structure; you can simplify the list of opt-ins at first.

**Why:** One place for “what every Android library looks like” (SDK, Java, Kotlin). When we add more convention plugins later (e.g. for Compose), they can reuse the same functions.

---

#### Step 2.4 — Android library convention plugin and registration

**What you’re doing:** Creating the plugin class that every Android library module will apply. It applies the official Android library and Kotlin Android plugins, then configures them using the shared functions from step 2.3. Then you register this class as a Gradle plugin with a stable id so modules can use `id("...")`.

**What to do:**

1. In the same package as `KotlinAndroid.kt`, create **`AndroidLibraryConventionPlugin.kt`** (or a similar name).

2. Implement **`Plugin<Project>`** (from `org.gradle.api`). In `apply(target: Project)`:
   - Apply **`com.android.library`** and **`org.jetbrains.kotlin.android`** (with `pluginManager.apply("...")`).
   - Get the Android extension: `extensions.configure<LibraryExtension> { configureKotlinAndroid(this); defaultConfig.targetSdk = 36 }`. (`LibraryExtension` is from `com.android.build.gradle.LibraryExtension`.)
   - Get the Kotlin extension: `extensions.getByType<KotlinAndroidProjectExtension>().apply { configureKotlinAndroid(this) }`.

   Reference: `D:\Study\Android\SkyDove\pokedex-compose-04-02-2026\build-logic\convention\src\main\kotlin\AndroidLibraryConventionPlugin.kt`.

3. **Register the plugin** in **`build-logic/convention/build.gradle.kts`**:
   - Add a **`gradlePlugin { plugins { ... } }`** block.
   - Inside: `register("androidLibrary") { id = "ashraf.pokedex.mad.android.library"; implementationClass = "ashraf.pokedex.mad.AndroidLibraryConventionPlugin" }` (use your package and class name).
   - So when a module applies `id("ashraf.pokedex.mad.android.library")`, Gradle instantiates `AndroidLibraryConventionPlugin` and calls `apply(project)`.

**Why:** This is the plugin that makes `core:model` (and every other Android library) only need one line plus `namespace` and dependencies. The plugin does the rest.

---

#### Step 2.5 — Sync and verify

**What you’re doing:** Confirming that the root project sees the convention plugin and the build compiles.

**What to do:**

- Run **Gradle sync** from the root project (e.g. “Sync Project with Gradle Files” in Android Studio).
- Fix any errors (e.g. wrong package/class name in `implementationClass`, or missing `compileOnly` for AGP/Kotlin).
- You do **not** need to apply the plugin in any module yet; that happens in Phase 3 when you add `core:model`. If sync succeeds and you see no “Plugin with id ‘ashraf.pokedex.mad.android.library’ not found” when you later apply it, Phase 2 is done.

**Why:** Catching configuration mistakes now avoids confusion when you add the first library module in Phase 3.

---

### Phase 2.6 — Hilt and Spotless convention plugins (align with reference)

**Concept:** The reference project (pokedex-compose) does not apply Hilt/KSP or Spotless directly in each module. Instead it uses two extra convention plugins: one that applies Hilt + KSP + Hilt dependencies in a single `id("...")`, and one that applies Spotless with shared formatting rules. We mirror that so every module stays minimal and consistent.

**Why these are needed:**

| Convention plugin | What it does | Why use it instead of applying in each module |
|-------------------|--------------|-----------------------------------------------|
| **android.hilt** (`ashraf.pokedex.mad.android.hilt`) | Applies the Hilt Gradle plugin, the KSP plugin, and adds `implementation(hilt-android)`, `implementation(androidx.hilt.navigation.compose)`, and `ksp(hilt-compiler)`. | Every module that needs DI (app, core:network, later core:database, feature modules) would otherwise repeat the same three plugin lines and three dependency lines. One id gives you all of it; version and libs stay in one place (the plugin and catalog). |
| **spotless** (`ashraf.pokedex.mad.spotless`) | Applies the Spotless Gradle plugin and configures it: Kotlin/kts/xml targets, ktlint rules, license headers from root `spotless/spotless.license.kt` and `spotless.license.xml`, trim trailing whitespace, end with newline. | Ensures the same code style and license header in every module without copying the same long `spotless { }` block into each `build.gradle.kts`. Run `./gradlew spotlessApply` once to fix existing files; `spotlessCheck` can run in CI. |

**What was done (for reference):**

1. **Version catalog** (`gradle/libs.versions.toml`): Added `spotless`, `androidxHiltNavigationCompose`; libraries `androidx-hilt-navigation-compose`, `spotless-gradlePlugin`; plugin `spotless`.
2. **Build-logic:** Added `AndroidHiltConventionPlugin.kt` (applies Hilt + KSP, adds the three deps from catalog) and `SpotlessConventionPlugin.kt` (applies Spotless, configures ktlint + license headers for kotlin/kts/xml). Registered both in `build-logic/convention/build.gradle.kts`. Added `compileOnly(libs.spotless.gradlePlugin)` so the Spotless plugin compiles.
3. **Root:** Added `spotless/spotless.license.kt` and `spotless/spotless.license.xml` (license headers; Spotless injects them into sources).
4. **App:** Replaced direct Hilt/KSP and Hilt deps with `id("ashraf.pokedex.mad.android.hilt")` and `id("ashraf.pokedex.mad.spotless")`.
5. **core:model:** Added `id("ashraf.pokedex.mad.spotless")` only (no Hilt in this module).
6. **core:network:** Replaced direct Hilt/KSP and Hilt deps with `id("ashraf.pokedex.mad.android.hilt")` and `id("ashraf.pokedex.mad.spotless")`.

After this, module `plugins { }` blocks match the reference style: one line per concern (library, hilt, spotless, serialization) instead of repeating the same Gradle and dependency boilerplate everywhere.

---

### Review vs reference (before proceeding to Phase 4.1.3)

Use this checklist to confirm nothing is missed compared to the reference project for what we've built so far.

**Aligned (no action):**

| Area | Reference | Yours | Notes |
|------|-----------|--------|--------|
| settings.gradle.kts | includeBuild, TYPESAFE_PROJECT_ACCESSORS, includes for app + core + features | Same | Includes **`feature:home`**, **`feature:settings`**, datastore, test, etc. |
| Root build.gradle.kts | apply false for app, library, kotlin, hilt, ksp, etc. | Same | Reference also applies Spotless at root; we apply per-module — both valid. |
| App | Hilt + Spotless via convention plugins; Application @HiltAndroidApp | Same | Reference also uses application + application.compose convention plugins; we apply android/kotlin/compose in app explicitly — OK. |
| core:model | library + serialization + spotless, namespace, kotlinx.serialization.json | Same | Reference adds parcelize, ksp, stable.marker, immutable — see Optional below. |
| core:network | library + hilt + spotless + serialization, Retrofit, OkHttp, coroutines, core:model | Same | Reference has buildConfig = true, core:test, Sandwich; we have buildConfig commented — see below. |
| build-logic | androidLibrary, androidHilt, spotless registered; Spotless as implementation for runtime | Same | Reference may use compileOnly(spotless); we use implementation so SpotlessExtension loads — documented. |

**Optional / add later (not required before 4.1.3):**

| Item | Reference | When to add |
|------|-----------|-------------|
| Root Spotless | Reference applies `alias(libs.plugins.spotless)` at root (no apply false). | Optional; we apply Spotless per module via convention plugin. Add at root only if you want root scripts formatted the same way. |
| core:model — kotlin.parcelize | Reference has it. | When you need Parcelable (e.g. passing Pokemon via Navigation or savedStateHandle). |
| core:model — ksp | Reference has it. | Only if you add a dependency that uses KSP in core:model; skip for now. |
| core:model — compose.stable.marker, kotlinx.immutable.collection | Reference has compileOnly(stable.marker), api(immutable.collection). | When you want Compose stability checking or immutable collections in model; not required for PokeAPI flow. |
| Application convention plugin | Reference has android.application + android.application.compose so app has no android { } block. | Phase 5 or later if you want app build to be minimal like reference; current app config is fine. |
| core:network — core:test, mockwebserver | Reference has testImplementation(core.test), mockwebserver, arch.core.testing. | **Done** — `:core:test` + network tests; add extras (e.g. Arch) only if needed. |

**Do before or while doing 4.1.3–4.1.6:**

| Item | Action |
|------|--------|
| core:network — buildConfig | Uncomment `buildFeatures { buildConfig = true }` in `core/network/build.gradle.kts` when you add NetworkModule.kt that uses `BuildConfig.DEBUG` for logging (e.g. HttpLoggingInterceptor). |
| app — core:network dependency | Add `implementation(projects.core.network)` in app in step 4.1.6 after NetworkModule exists. |

**Summary:** You are in adherence with the reference for Phases 1, 2, 3, 4.0, and 4.1 (build/config). The only thing to remember when you add NetworkModule is to enable `buildConfig` in core:network if you use `BuildConfig.DEBUG`. No missing steps for the current scope.

---

## Phase 3: First core module — `core:model`

**Concept:** `core:model` holds shared data classes (e.g. `Pokemon`). No Android UI, Room, or Retrofit—only Kotlin and kotlinx.serialization. Because you have the convention plugin (Phase 2), this module’s `build.gradle.kts` is minimal: apply the plugin + serialization, set `namespace`, add dependencies. No compileSdk/minSdk in this file.

| Step | Task | Status |
|------|------|--------|
| 3.1 | Create folders: `core/model/`, `core/model/build.gradle.kts`, `core/model/src/main/kotlin/` + your package path (e.g. `ashraf/pokedex/mad/core/model/`). | ✅ |
| 3.2 | In `settings.gradle.kts`, add: `include(":core:model")`. | ✅ |
| 3.3 | In `core/model/build.gradle.kts`: apply your **convention plugin** (e.g. `id("yourproject.android.library")`), `alias(libs.plugins.kotlinx.serialization)`. Set `namespace`. Add `implementation(libs.kotlinx.serialization.json)` only. No compileSdk/minSdk here—convention plugin provides them. | ✅ |
| 3.4 | Create one model class (e.g. `Pokemon.kt`) in the package with a few fields; add `@Serializable`. | ✅ |
| 3.5 | Sync. Optionally in `app`: `implementation(projects.core.model)` and use the model in a composable to verify. | ✅ |

---

## Phase 4: More core modules (network, database, data)

**Concept:** Add the data layer: **core:network** (Retrofit + PokeAPI), **core:database** (Room cache), **core:data** (repositories that use both). ViewModels and UI will depend only on **core:data**; they never touch Retrofit or Room directly.

**Order:** 4.0 (Hilt in app) → 4.1 (network) → 4.2 (database) → **4.3 (core:common)** → **4.4 (core:data)** → 4.5 (app convention plugin). We do **core:common before core:data** so that PokedexClient and repositories can follow the reference structure from the start.

---

### Phase 4.0 — Hilt in the app (prerequisite for 4.1)

| Step  | Task                                                                                                                                                                                                                        | Status |
|-------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------|
| 4.0.1 | In root `build.gradle.kts`, add `alias(libs.plugins.hilt.plugin) apply false` and `alias(libs.plugins.ksp) apply false`. In `app/build.gradle.kts`, in `plugins { }` add `alias(libs.plugins.hilt.plugin)` and `alias(libs.plugins.ksp)`; in `dependencies { }` add `implementation(libs.hilt.android)` and `ksp(libs.hilt.compiler)`. (KSP plugin is required for `ksp(...)` to resolve.) | ✅      |
| 4.0.2 | Create an `Application` class annotated with `@HiltAndroidApp`. Register it in AndroidManifest with `android:name`.                                                                                                         | ✅      |
| 4.0.3 | Sync; app should build. No modules need to inject anything yet.                                                                                                                                                             | ✅      |

---

### Phase 4.1 — core:network

| Step | Task | Status |
|------|------|--------|
| 4.1.1 | Create `core/network/` with `build.gradle.kts`, `src/main/AndroidManifest.xml`, `src/main/kotlin/` + package path (e.g. `ashraf/pokedex/mad/core/network/`). Add `include(":core:network")` in `settings.gradle.kts`. | ✅ |
| 4.1.2 | In `core/network/build.gradle.kts`: apply convention plugin, kotlinx.serialization; set namespace. Deps: `implementation(projects.core.model)`, Retrofit (BOM + retrofit + converter-kotlinx-serialization), OkHttp (BOM + logging-interceptor), kotlinx.serialization.json, coroutines-android. Optional: Hilt (so NetworkModule can provide Retrofit). | ✅ |
| 4.1.3 | Add **PokemonResponse.kt** in `core/network` (e.g. in `model/` subpackage): `@Serializable` data class with `count`, `next`, `previous`, `results: List<Pokemon>`. Uses `Pokemon` from core:model. | ✅ |
| 4.1.4 | Add **PokedexService.kt**: Retrofit interface with `@GET("pokemon") suspend fun fetchPokemonList(@Query("limit") limit, @Query("offset") offset): PokemonResponse`. Base URL will be `https://pokeapi.co/api/v2/`. | ✅ |
| 4.1.5 | Add **NetworkModule.kt** (Hilt): provide `Json`, `OkHttpClient`, `Retrofit`, `PokedexService`. Base URL `https://pokeapi.co/api/v2/`, kotlinx.serialization converter. Enable `buildConfig = true` in core:network if you use BuildConfig.DEBUG for logging. | ✅ |
| 4.1.6 | In `app/build.gradle.kts` add `implementation(projects.core.network)`. Sync; app should build (no need to use the service in UI yet). | ✅ |

**Reference:** `core/network/` in pokedex-compose (build.gradle.kts, PokedexService, PokemonResponse, NetworkModule).

---

### Phase 4 — Step-by-step walkthrough (4.0 + 4.1)

**Phase 4.0 — Hilt in the app**

1. **Root `build.gradle.kts`:** In `plugins { }` add `alias(libs.plugins.hilt.plugin) apply false` and `alias(libs.plugins.ksp) apply false`.
2. **`app/build.gradle.kts`:** Either (a) use the **Hilt convention plugin** (Phase 2.6): add `id("ashraf.pokedex.mad.android.hilt")` in `plugins { }` and do *not* add Hilt/KSP or Hilt deps in dependencies — the plugin adds them; or (b) without convention plugin: add `alias(libs.plugins.hilt.plugin)` and `alias(libs.plugins.ksp)` in `plugins { }`, and `implementation(libs.hilt.android)` and `ksp(libs.hilt.compiler)` in `dependencies { }`.
3. **Application class:** Create a class (e.g. `PokedexApplication`) in the app module, in the same package as your app. Annotate it with `@HiltAndroidApp` (from `dagger.hilt.android.HiltAndroidApp`). Extend `Application`. In `AndroidManifest.xml`, add `android:name=".PokedexApplication"` (or your class name) on the `<application>` tag.
4. **Sync** — the app should compile. Hilt will generate DI code; you don’t need to inject anything in the UI yet.

**Phase 4.1 — core:network**

1. **Create the module structure (4.1.1)**  
   - Create folders: `core/network/`, `core/network/build.gradle.kts`, `core/network/src/main/kotlin/ashraf/pokedex/mad/core/network/` (and optionally `model/`, `service/`, `di/` subpackages).  
   - Create `core/network/src/main/AndroidManifest.xml` with `<manifest />`.  
   - In root `settings.gradle.kts`, add `include(":core:network")`.

2. **`core/network/build.gradle.kts` (4.1.2)**  
   - Plugins: `id("ashraf.pokedex.mad.android.library")`, `alias(libs.plugins.kotlinx.serialization)`.  
   - Optional: Hilt so you can provide Retrofit from a module — add `id("ashraf.pokedex.mad.android.hilt")` (and `id("ashraf.pokedex.mad.spotless")` if you use Spotless; see Phase 2.6). The Hilt convention plugin applies Hilt + KSP and adds the Hilt deps.  
   - `android { namespace = "ashraf.pokedex.mad.core.network" }`. If you use `BuildConfig.DEBUG` in NetworkModule, add `buildFeatures { buildConfig = true }`.  
   - Dependencies: `implementation(projects.core.model)`, `implementation(platform(libs.retrofit.bom))`, `implementation(libs.retrofit)`, `implementation(libs.retrofit.kotlinx.serialization)`, `implementation(platform(libs.okhttp.bom))`, `implementation(libs.okhttp.logging.interceptor)`, `implementation(libs.kotlinx.serialization.json)`, `implementation(libs.kotlinx.coroutines.android)`.

3. **PokemonResponse.kt (4.1.3)**  
   - Package: `ashraf.pokedex.mad.core.network.model` (or `...core.network`).  
   - `@Serializable data class PokemonResponse(val count: Int, val next: String?, val previous: String?, val results: List<Pokemon>)`. Use `@SerialName` if JSON keys differ (e.g. `"results"`). Import `Pokemon` from `ashraf.pokedex.mad.core.model`.

4. **PokedexService.kt (4.1.4)**  
   - Package: `ashraf.pokedex.mad.core.network.service`.  
   - Retrofit interface with one method: `@GET("pokemon") suspend fun fetchPokemonList(@Query("limit") limit: Int = 20, @Query("offset") offset: Int = 0): PokemonResponse`. Base URL will be set in NetworkModule as `https://pokeapi.co/api/v2/`.

5. **NetworkModule.kt (4.1.5)**  
   - Package: `ashraf.pokedex.mad.core.network.di`.  
   - Hilt module: `@Module @InstallIn(SingletonComponent::class) object NetworkModule`.  
   - Provide `Json` (e.g. `Json { ignoreUnknownKeys = true }`).  
   - Provide `OkHttpClient` (add `HttpLoggingInterceptor` in debug if you use BuildConfig).  
   - Provide `Retrofit`: baseUrl `"https://pokeapi.co/api/v2/"`, add `json.asConverterFactory("application/json".toMediaType())`, create with `client(okHttpClient)`.  
   - Provide `PokedexService`: `retrofit.create(PokedexService::class.java)`.

6. **App depends on core:network (4.1.6)**  
   - In `app/build.gradle.kts`, add `implementation(projects.core.network)`. Sync; app should build. You can later inject `PokedexService` in a ViewModel or use it from a repository in core:data.

---

### Phase 4.2 — core:database

**Concept:** Room cache for the Pokemon list (and later detail). `core:data` will read from the DAO and write from network. We keep Room-specific types in `core:database` and map to/from `core:model.Pokemon`.

| Step | Task | Status |
|------|------|--------|
| 4.2.1 | Create `core/database/` with `build.gradle.kts`, `src/main/AndroidManifest.xml`, `src/main/kotlin/` + package path (e.g. `ashraf/pokedex/mad/core/database/`). Add `include(":core:database")` in `settings.gradle.kts`. | ✅ |
| 4.2.2 | In `core/database/build.gradle.kts`: apply `id("ashraf.pokedex.mad.android.library")`, `id("ashraf.pokedex.mad.android.hilt")`, `id("ashraf.pokedex.mad.spotless")`, `alias(libs.plugins.ksp)`. Set `namespace = "ashraf.pokedex.mad.core.database"`. In `android { defaultConfig { ksp { arg("room.schemaLocation", "$projectDir/schemas") } } }` and in `sourceSets["test"]` add `assets.srcDir("$projectDir/schemas")`. Deps: `implementation(projects.core.model)`, `implementation(libs.androidx.room.runtime)`, `implementation(libs.androidx.room.ktx)`, `ksp(libs.androidx.room.compiler)`, `implementation(libs.kotlinx.coroutines.android)`. | ✅ |
| 4.2.3 | Add **PokemonEntity** in `entity/` (tableName = `"pokemon"`, fields: `page: Int`, `name: String` as `@PrimaryKey`, `url: String`). Add **PokemonDao** (insert list, get page, get all up to page). Add **PokedexDatabase** (`@Database(entities = [PokemonEntity], version = 1, exportSchema = true)`) with `abstract fun pokemonDao(): PokemonDao`. | ✅ |
| 4.2.4 | Add Hilt **DatabaseModule** in `di/` that provides `PokedexDatabase` (using `Room.databaseBuilder(application, PokedexDatabase::class.java, "Pokedex.db").fallbackToDestructiveMigration().build()`) and `PokemonDao` from the database. | ✅ |
| 4.2.5 | Add an **EntityMapper** interface and **PokemonEntityMapper** in `entity/mapper/` that converts between `List<Pokemon>` and `List<PokemonEntity>` plus extension functions `List<Pokemon>.asEntity()` and `List<PokemonEntity>?.asDomain()`. This mirrors the reference and keeps Room types out of `core:model`. | ✅ |
| 4.2.6 | In `app/build.gradle.kts` add `implementation(projects.core.database)`. Sync; app should build (no UI change yet). | ✅ |

**Reference:** `core/database/` in pokedex-compose (build.gradle.kts, PokemonEntity, PokemonDao, PokedexDatabase, DatabaseModule, entity mappers). We will start with only the list entity/DAO; detail entities and converters can be added later when we implement the detail screen.

---

## Phase 4.3 — core:common (utilities) + PokedexClient in core:network

**Concept:** Introduce a JVM-only `core:common` module like the reference project. It holds Hilt qualifiers and modules for dispatchers and app CoroutineScope (no Android dependency). Then add `PokedexClient` in `core:network` so repositories inject it instead of `PokedexService` directly.

**When:** After 4.2 (core:database), before 4.4 (core:data). This order lets core:data use PokedexClient from the start.

| Step | Task | Status |
|------|------|--------|
| 4.3.1 | Create `core/common/` as a **pure Kotlin/JVM** module. In `settings.gradle.kts` add `include(":core:common")`. In `core/common/build.gradle.kts`: use **`kotlin("jvm")`** (not `alias(libs.plugins.kotlin.jvm)` — see note below), `alias(libs.plugins.ksp)`. Deps: `implementation(libs.hilt.core)`, `implementation(libs.kotlinx.coroutines.core)`, `ksp(libs.hilt.compiler)`. No AndroidManifest; no `android {}` block. | ✅ |
| 4.3.2 | **Strict mirror of reference** `core/common`: add in package `ashraf.pokedex.mad.core.common.network` — **PokedexAppScope.kt** (qualifier), **PokedexAppDispatchers.kt** (qualifier `@Dispatcher` + enum `PokedexAppDispatchers { IO }`), **DispatchersModule.kt** (provides IO dispatcher), **CoroutineScopesModule.kt** (provides app-wide `CoroutineScope` with `@PokedexAppScope`). Copy structure from reference `core/common/src/main/java/.../core/common/network/`. | ✅ |
| 4.3.3 | In **core:network**, add **PokedexClient** in package `ashraf.pokedex.mad.core.network.service`: inject `PokedexService`, expose `suspend fun fetchPokemonList(page: Int): ApiResponse<PokemonResponse>` (page → limit/offset internally, e.g. page size 20). Ensure `core:network` has Sandwich dependency. | ✅ |
| 4.3.4 | (Done in Phase 4.4.) When implementing **HomeRepositoryImpl** in core:data, inject **PokedexClient** (not PokedexService) and use its `fetchPokemonList(page)`; handle `ApiResponse` with Sandwich operators in the repository. | ✅ |

**Note (4.3.1):** The reference uses `alias(libs.plugins.kotlin.jvm)`. In this project the Kotlin plugin is already on the classpath (build-logic/root), so applying the JVM plugin with a version via the catalog causes a conflict. Use `kotlin("jvm")` in `core/common/build.gradle.kts` instead; behavior is the same.

---

### Phase 4.4 — core:data

**Concept:** Data layer that combines **core:network** (Retrofit + Sandwich) and **core:database** (Room) behind repository interfaces. UI/ViewModels depend only on core:data; they never talk to Retrofit or Room directly. Start with the home/list flow first; detail flow and tests come in later subphases.

| Step | Task | Status |
|------|------|--------|
| 4.4.1 | Create `core/data/` with `build.gradle.kts`, `src/main/AndroidManifest.xml`, `src/main/kotlin/` + package path (e.g. `ashraf/pokedex/mad/core/data/`). Add `include(":core:data")` in `settings.gradle.kts`. | ✅ |
| 4.4.2 | In `core/data/build.gradle.kts`: apply `id("ashraf.pokedex.mad.android.library")`, `id("ashraf.pokedex.mad.android.hilt")`, `id("ashraf.pokedex.mad.spotless")`. Set `namespace = "ashraf.pokedex.mad.core.data"`. Deps: `implementation(projects.core.model)`, `implementation(projects.core.network)`, `implementation(projects.core.database)`, `implementation(libs.kotlinx.coroutines.android)`. | ✅ |
| 4.4.3 | Define a **HomeRepository** interface in `core/data` (e.g. `fetchPokemonList(page, onStart, onComplete, onLastPageReached, onError): Flow<List<Pokemon>>`), mirroring the reference shape. | ✅ |
| 4.4.4 | Implement **HomeRepositoryImpl**: inject `PokedexClient` and `PokemonDao`; implement offline-first flow similar to reference: try DB; if empty, call network (Sandwich ApiResponse), on success store to DB and emit; on failure, call `onError`. Use mappers `List<Pokemon>.asEntity()` / `List<PokemonEntity>?.asDomain()`. | ✅ |
| 4.4.5 | Add a **DataModule** (Hilt) in `core/data` that binds `HomeRepository` to `HomeRepositoryImpl` (e.g. with `@Binds` or `@Provides`). | ✅ |
| 4.4.6 | In `app/build.gradle.kts` add `implementation(projects.core.data)`. Later, ViewModels in feature modules will inject `HomeRepository` and expose UI state. | ✅ |

**Reference:** `core/data` in pokedex-compose (HomeRepositoryImpl, DetailsRepositoryImpl, DI bindings). This repo includes **home + details + user-data** repositories and **Phase 6** unit tests for the main repository surfaces.

---

## Phase 4.5 — Build-logic upgrade: App convention plugin (recommended)

**Concept:** Align the app module with the reference by moving shared Android/Kotlin/Compose config (compileSdk/minSdk/targetSdk, Java 17, Compose setup) into **convention plugins**. This keeps `app/build.gradle.kts` minimal and consistent as the project grows.

**When:** Do this **right after Phase 4.4 (core:data)** and **before Phase 5**.

| Step | Task | Status |
|------|------|--------|
| 4.5.1 | In `build-logic/convention/`, add an **application** convention plugin (e.g. `AndroidApplicationConventionPlugin.kt`) that applies `com.android.application` + `org.jetbrains.kotlin.android` and configures compileSdk/minSdk/targetSdk + Java 17. Register it with id `ashraf.pokedex.mad.android.application`. | ✅ |
| 4.5.2 | (Optional) Add an **application.compose** convention plugin that enables Compose (`buildFeatures.compose = true`), sets `composeOptions.kotlinCompilerExtensionVersion` if needed, and adds Compose BOM + core Compose deps. Register it with id `ashraf.pokedex.mad.android.application.compose`. | ✅ |
| 4.5.3 | Update `app/build.gradle.kts` to use the new convention plugin id(s) and delete duplicated `android { }` config that is now provided by build-logic (keep app-specific config only). | ✅ |
| 4.5.4 | Sync and run `:app:assembleDebug` to confirm the app still builds. | ✅ |

---

## Phase 5: Design system, navigation, ViewModel, previews — then features

**Concept:** Match the reference: **design system** (theme) → **navigation** (routes/graph) → then **ViewModels + screens** with **`@Preview`** wrapped in the same `Theme`. List/detail data use **Room + repositories**; **DataStore** backs **user preferences** (theme, etc.) via **`core:datastore`** + **settings** (see 5.7).

### Recommended order (do before heavy home/detail coding)

| Order | Focus | Why |
|-------|--------|-----|
| 1 | **Design system** | Single theme for app + all previews. |
| 2 | **Navigation** | Stable routes before screens multiply. |
| 3 | **ViewModel + UI** | Home (then detail) inside the graph. |
| 4 | **Preview** | Add per composable (`Theme { … }` + fake state), not as one giant “phase.” |

| Step | Task | Status |
|------|------|--------|
| 5.1 | **Design system module** — reference uses `feature:designsystem`; here **`core:designsystem`**: Compose + Spotless; `PokedexTheme`, colors, typography; **`android.feature`** convention for feature modules. | ✅ |
| 5.2 | **`app`** (and features): depend on **`core:designsystem`**; root UI wrapped in **`PokedexTheme`** (see `PokedexMain`). | ✅ |
| 5.3 | **Navigation 3**: `PokedexScreen`, `PokedexNavHost` in `app`, `NavDisplay`, home → details → settings dialog. | ✅ |
| 5.4 | **`feature:home`**: `HomeViewModel`, `PokedexHome`, list/loading/error, previews / `FakeHomeRepository` where needed. | ✅ |
| 5.5 | **Details**: `DetailsViewModel` (`feature:home` details package), `PokedexDetails`, **`DetailsRepository`** + **`PokemonInfo`** Room path in **`core:database`**. | ✅ |
| 5.6 | **Landscapist**: re-exported from **`core:designsystem`** for list/detail images. | ✅ |

### 5.7 — DataStore + **settings** (and other preference UIs)

**You’re right:** the roadmap did not name a **settings screen** before — DataStore is most often **read/written from a Settings screen**, but it is **not only** for that.

**Do not block Phase 5.4 (home list) on DataStore.** Room + `HomeRepository` already cover list cache.

| Step | Task | Status |
|------|------|--------|
| 5.7.1 | **`core:datastore`**: proto **UserPreferences** + `PreferencesDataSource`; Hilt wiring from **`core:data`** (`UserDataRepositoryImpl`). | ✅ |
| 5.7.2 | **`feature:settings`**: `SettingsViewModel`, `PokedexSettings`, theme selection; entry from home app bar. | ✅ |

**Where DataStore is needed *besides* settings UI**

| Use case | Needs a “settings screen”? |
|----------|----------------------------|
| Theme (light/dark/system) — user picks in Settings | Yes, usually |
| Same theme pref — written once from onboarding, no settings yet | No — only onboarding flow |
| “Don’t show onboarding again” | No — splash/onboarding only |
| Feature flags / analytics opt-in stored on device | Sometimes Settings; sometimes one-time dialog |

So: **settings screen = main place users edit prefs**; **DataStore = storage** that can also be updated from onboarding, dialogs, or first-run without a dedicated settings UI yet.

**Shortcut (acceptable):** minimal home in `app` with default `MaterialTheme`, then migrate to 5.1–5.2 — but your preferred path is **design system + navigation first**, then code home.

---

## Phase 6: Testing

| Area | Done |
|------|------|
| `:core:test` | Helpers (`MainCoroutinesRule`, `MockUtil`, …). |
| `:core:network` / `:core:database` / `:core:datastore` | Service, DAO, serializer / data source tests. |
| `:core:data` | `UserDataRepository`, `HomeRepositoryImpl`, `DetailsRepository` tests. |
| `:feature:home` / `:feature:settings` | `HomeViewModel`, `DetailsViewModel`, `SettingsViewModel` tests. |

---

## Phase 7: Code quality (Spotless)

**Status: ✅** — Spotless is already applied via the **Spotless convention plugin** (Phase 2.6): each module that applies `id("ashraf.pokedex.mad.spotless")` gets the same formatting and license-header rules. Root license files are in `spotless/spotless.license.kt` and `spotless/spotless.license.xml`. Run `./gradlew spotlessApply` to fix existing files; use **`spotlessCheck` in CI** when Phase 8 is added. No extra Spotless steps are required unless you want to add more rules or formats.

---

## Phase 8: CI/CD (GitHub Actions) + release hardening

This phase combines **automation in CI** with **release-quality builds** (shrinking, rules, signing). Baseline profiles for *startup* stay in **Phase 9**; here we focus on **APK size / R8** and **shipping a release**.

### 8.1 CI (GitHub Actions)

- Add a workflow that runs on PR / push: `./gradlew test`, `spotlessCheck`, and `:app:assembleDebug` (or your chosen variant).
- Optionally run **`assembleRelease`** on `main` or tags so shrinker issues surface early (see 8.2).
- Cache Gradle (`~/.gradle`) to keep CI fast.

### 8.2 Code shrinking & optimization (R8)

- In **`app`** `release` (or a dedicated `minified` buildType): set **`isMinifyEnabled = true`** when you are ready to debug R8 (start with `false` until you have rules).
- Optionally **`isShrinkResources = true`** after minify is stable (removes unused resources; requires careful testing).
- Maintain **`proguard-rules.pro`** (app) and **`consumer-rules.pro`** in libraries (e.g. **`:core:datastore`** protobuf lite keeps) so **DataStore / Retrofit / Hilt** keep working under shrink.
- Run **`./gradlew :app:assembleRelease`** locally and smoke-test: cold start, network, Room, settings/theme, navigation.

### 8.3 Signing & secrets

- Add **release signing** (`signingConfigs`) using **local** `keystore.properties` or CI secrets — never commit keystores or passwords to git.
- Document how you build a release AAB/APK for yourself or Play Console.

### 8.4 CI + release together

- CI should at least **compile** release (or run R8 on a nightly job) so regressions in ProGuard rules are caught before you cut a store build.

---

## Phase 9: Baseline profiles

*Steps will be added here when we start this phase.*

---

### Phase 4 — Path we followed (summary for learners)

| Order | Phase   | What we did |
|-------|---------|-------------|
| 1     | 4.0     | Hilt in app (Application + manifest). |
| 2     | 4.1     | core:network (Retrofit, PokedexService, PokemonResponse, NetworkModule). |
| 3     | 4.2     | core:database (Room, PokemonEntity, PokemonDao, DatabaseModule, entity mappers). |
| 4     | **4.3** | **core:common** (JVM module with `kotlin("jvm")` + KSP + Hilt core; then PokedexAppScope, Dispatcher/PokedexAppDispatchers, DispatchersModule, CoroutineScopesModule). **PokedexClient** in core:network (service package, `fetchPokemonList(page)`). |
| 5     | **4.4** | **core:data** (module shell, HomeRepository interface, then HomeRepositoryImpl using PokedexClient + PokemonDao, DataModule, app depends on core:data). |
| 6     | 4.5     | App convention plugin (optional, before Phase 5). |

**Adjustments made along the way:** (1) core:common uses `kotlin("jvm")` instead of catalog alias for Kotlin JVM plugin to avoid classpath conflict.

---

*Last updated: Phases 1–7 marked complete in tables; **Phase 8 (CI + release)** is next; **Phase 9** baseline profiles after that. Reference parity table and `TESTING_PLAN.md` out-of-scope section restored if edited locally.*
