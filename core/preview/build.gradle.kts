plugins {
    // Base Android setup for library modules (compileSdk/minSdk/Java/Kotlin defaults, etc.)
    id("ashraf.pokedex.mad.android.library")

    // Enables Jetpack Compose for this *library* module (buildFeatures + compose plugin).
    // Reference uses a separate compose convention plugin too.
    id("ashraf.pokedex.mad.android.library.compose")

    // Hilt in one line (applies hilt + ksp + adds hilt dependencies).
    // Reference includes this for preview module, so we mirror it for parity.
    id("ashraf.pokedex.mad.android.hilt")

    // Shared formatting + license headers.
    id("ashraf.pokedex.mad.spotless")
}

android {
    // Namespace for this module (controls the R class + generated resources)
    // Use your core module namespace so it’s consistent.
    namespace = "ashraf.pokedex.mad.core.preview"
}

dependencies {
    // ----- core modules -----

    // Theme / colors / typography tokens used by Preview wrappers.
    // Purpose: previews should render with the correct app look.
    implementation(projects.core.designsystem)

    // Navigation contracts/types used by preview screens (if any).
    // Purpose: preview can build composables that reference navigation types.
    implementation(projects.core.navigation)

    // Domain models used by preview UI state/samples.
    implementation(projects.core.model)
    
}