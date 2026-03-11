plugins {
    // Applies shared Android library defaults (compileSdk/minSdk/Java 17/Kotlin options).
    id("ashraf.pokedex.mad.android.library")

    // Applies Hilt + KSP + Hilt deps via our convention plugin.
    // Needed because core:data will provide/bind repositories for injection.
    id("ashraf.pokedex.mad.android.hilt")

    // Applies Spotless formatting + license headers consistently across modules.
    id("ashraf.pokedex.mad.spotless")
}

android {
    // Unique package namespace for this module's R class + manifest merging.
    namespace = "ashraf.pokedex.mad.core.data"
}

dependencies {
    // Domain models shared across layers (Pokemon, etc.)
    implementation(projects.core.model)

    // Network layer (Retrofit + service + Sandwich wrappers).
    // core:data will call this and map results into domain models.
    implementation(projects.core.network)

    // Database layer (Room DAO + entities + mappers).
    // core:data will use this for caching/offline-first behavior.
    implementation(projects.core.database)

    // Repository implementations use coroutines/Flow.
    implementation(libs.kotlinx.coroutines.android)

    // Sandwich: handy operators for handling API responses (Success / Error).
    implementation(libs.sandwich)

    // Not added yet (we'll add exactly when needed):
    // - Test deps (turbine/mockito/coroutines-test): Phase 6
}