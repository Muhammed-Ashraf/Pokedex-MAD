// =============================================================================
// CORE:MODEL — Step 3.3
// =============================================================================
// This module holds shared data classes (e.g. Pokemon) used by network, database,
// and UI. It has no Android UI, Room, or Retrofit. We apply the convention plugin
// so compileSdk, minSdk, Java 17, etc. come from build-logic; we only set what's
// specific to this module: namespace and dependencies.
// =============================================================================

plugins {
    // One line gives this module: android { }, Kotlin, compileSdk 36, minSdk 29, Java 17.
    // No need to repeat those here — the convention plugin (Phase 2) does it.
    id("ashraf.pokedex.mad.android.library")

    // Needed for @Serializable on model classes (e.g. Pokemon) so we can serialize
    // to/from JSON when using Retrofit or saving to disk.
    alias(libs.plugins.kotlinx.serialization)

    id("ashraf.pokedex.mad.spotless")
}

android {
    // Must match the package you use under core/model/src/main/kotlin/.
    // Used for R class and manifest merger; keeps this module's resources distinct.
    namespace = "ashraf.pokedex.mad.core.model"
}

// Only what this module needs. No Room, Retrofit, Compose, or Hilt — model is pure data.
dependencies {
    // Kotlin Serialization JSON: runtime for @Serializable and Json.encode/decode.
    implementation(libs.kotlinx.serialization.json)
}
