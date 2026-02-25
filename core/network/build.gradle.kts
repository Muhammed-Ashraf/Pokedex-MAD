// =============================================================================
// CORE:NETWORK — Step 4.1.2
// =============================================================================
// This module: API models (PokemonResponse), Retrofit service (PokedexService),
// and Hilt NetworkModule. No UI. Convention plugin gives compileSdk/minSdk/Java;
// we add only what this module needs.
// =============================================================================

plugins {
    // Same as core:model — Android library + Kotlin + compileSdk 36, minSdk, Java 17.
    id("ashraf.pokedex.mad.android.library")
    // Hilt + KSP + deps (hilt.android, hilt.compiler, hilt-navigation-compose) in one plugin.
    id("ashraf.pokedex.mad.android.hilt")
    id("ashraf.pokedex.mad.spotless")
    // Needed for @Serializable on PokemonResponse and for Json in NetworkModule.
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "ashraf.pokedex.mad.core.network"
    // If you use BuildConfig.DEBUG in NetworkModule for logging, uncomment:
     buildFeatures { buildConfig = true }
}

dependencies {
    // --- Our model (Pokemon) lives here; PokemonResponse uses it.
    implementation(projects.core.model)

    // --- Retrofit: BOM pins versions for retrofit + converter; we add the artifacts we need.
    implementation(platform(libs.retrofit.bom))
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)

    // --- OkHttp: BOM + logging (for debug request/response logs).
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp.logging.interceptor)

    // --- JSON serialization (Retrofit converter uses this; @Serializable uses it too).
    implementation(libs.kotlinx.serialization.json)

    // --- Coroutines: Retrofit suspend functions and future repo/ViewModel use.
    implementation(libs.kotlinx.coroutines.android)
}
