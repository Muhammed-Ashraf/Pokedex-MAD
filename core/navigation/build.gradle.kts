plugins {
    // Base Android library conventions:
    // compileSdk/minSdk/Java/Kotlin defaults for all library modules.
    id("ashraf.pokedex.mad.android.library")

    // Enables Compose for library modules (buildFeatures.compose + compose plugin).
    id("ashraf.pokedex.mad.android.library.compose")

    // Needed for serializable route objects / payloads in navigation.
    alias(libs.plugins.kotlinx.serialization)

    // Hilt in navigation module (reference-aligned).
    // Useful when navigation-related classes need DI.
    id("ashraf.pokedex.mad.android.hilt")

    // Formatting + license headers consistency.
    id("ashraf.pokedex.mad.spotless")
}

android {
    // Package namespace for this module.
    namespace = "ashraf.pokedex.mad.core.navigation"
}

dependencies {
    // Shared domain models (used by typed routes / nav state if needed).
    implementation(projects.core.model)

    // Basic Android + coroutine support used by navigation classes/composables.
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    // Navigation3 libs exposed as API.
    // Why `api` (not implementation):
    // - Modules that depend on core:navigation can use navigation types directly
    //   without re-declaring these dependencies.
    // - This mirrors the reference project pattern.
    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.navigation3.ui)

    // JSON serialization support for route arguments/state objects.
    implementation(libs.kotlinx.serialization.json)
}