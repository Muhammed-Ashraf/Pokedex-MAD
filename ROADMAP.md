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
| 2.1 | Create `build-logic/` at project root. Add `build-logic/settings.gradle.kts` that includes a subproject (e.g. `convention`). Add `build-logic/convention/build.gradle.kts` with `kotlin-dsl`, Java 17, and `compileOnly` for AGP, Kotlin, Compose compiler (from root libs). | ⬜ |
| 2.2 | In root `settings.gradle.kts`, add `pluginManagement { includeBuild("build-logic") }` so the root build can apply your convention plugin ids. | ⬜ |
| 2.3 | In `build-logic/convention/`, create shared Kotlin config: e.g. `KotlinAndroid.kt` with `configureKotlinAndroid(CommonExtension)` (compileSdk, minSdk, Java 17, compileOptions) and `configureKotlinAndroid(KotlinAndroidProjectExtension)` (jvmTarget, freeCompilerArgs). | ⬜ |
| 2.4 | Create **Android library convention plugin**: applies `com.android.library` and `org.jetbrains.kotlin.android`, calls `configureKotlinAndroid` for both extensions. Register in `build-logic/convention/build.gradle.kts` with an id (e.g. `yourproject.android.library`). | ⬜ |
| 2.5 | Sync. Verify: root build can resolve the plugin (no need to use it in a module until Phase 3). | ⬜ |

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

## Phase 3: First core module — `core:model`

**Concept:** `core:model` holds shared data classes (e.g. `Pokemon`). No Android UI, Room, or Retrofit—only Kotlin and kotlinx.serialization. Because you have the convention plugin (Phase 2), this module’s `build.gradle.kts` is minimal: apply the plugin + serialization, set `namespace`, add dependencies. No compileSdk/minSdk in this file.

| Step | Task | Status |
|------|------|--------|
| 3.1 | Create folders: `core/model/`, `core/model/build.gradle.kts`, `core/model/src/main/kotlin/` + your package path (e.g. `ashraf/pokedex/mad/core/model/`). | ⬜ |
| 3.2 | In `settings.gradle.kts`, add: `include(":core:model")`. | ⬜ |
| 3.3 | In `core/model/build.gradle.kts`: apply your **convention plugin** (e.g. `id("yourproject.android.library")`), `alias(libs.plugins.kotlinx.serialization)`. Set `namespace`. Add `implementation(libs.kotlinx.serialization.json)` only. No compileSdk/minSdk here—convention plugin provides them. | ⬜ |
| 3.4 | Create one model class (e.g. `Pokemon.kt`) in the package with a few fields; add `@Serializable`. | ⬜ |
| 3.5 | Sync. Optionally in `app`: `implementation(projects.core.model)` and use the model in a composable to verify. | ⬜ |

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

*Last updated: Phases reordered — Phase 2 = Build logic, Phase 3 = core:model (minimal module).*
