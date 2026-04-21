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
    // BuildConfig is enabled so NetworkModule can gate logging via BuildConfig.DEBUG.
    buildFeatures { buildConfig = true }

    // JVM unit tests (src/test) do not run on a device; OkHttp touches android.util.Log unless
    // unmocked Android APIs return defaults. See: https://developer.android.com/r/studio-ui/build/not-mocked
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
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

    // --- Sandwich: wraps Retrofit responses in ApiResponse.Success / ApiResponse.Failure for consistent handling (see reference).
    implementation(libs.sandwich)

    // --- JSON serialization (Retrofit converter uses this; @Serializable uses it too).
    implementation(libs.kotlinx.serialization.json)

    // --- Coroutines: Retrofit suspend functions and future repo/ViewModel use.
    implementation(libs.kotlinx.coroutines.android)

    // --- Unit tests (reference core:network — no explicit junit here; core:test supplies JUnit on the test graph).

    // Shared test utilities: MainCoroutinesRule, MockUtil, etc. JUnit is pulled in via this module’s graph (see :core:test).
    testImplementation(projects.core.test)
    // runTest, TestDispatcher, and coroutine APIs used by ApiAbstract / service tests together with MainCoroutinesRule.
    testImplementation(libs.kotlinx.coroutines.test)
    // In-process fake HTTP server so Retrofit hits local URLs and tests enqueue JSON from src/test/resources (MockWebServer).
    testImplementation(libs.okhttp.mockwebserver)
    // InstantTaskExecutorRule: runs architecture-components background tasks synchronously in JVM unit tests (reference ApiAbstract).
    testImplementation(libs.androidx.arch.core.testing)
}
