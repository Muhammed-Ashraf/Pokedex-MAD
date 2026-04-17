// =============================================================================
// :core:datastore — Proto DataStore + protobuf code generation (reference-style)
// =============================================================================

plugins {
    // Android library defaults (compileSdk, minSdk, Kotlin, etc.)
    id("ashraf.pokedex.mad.android.library")

    // Hilt + KSP (for DataStoreModule, PreferencesDataSource @Inject, etc.)
    id("ashraf.pokedex.mad.android.hilt")

    // Formatting / license headers (same as other modules)
    id("ashraf.pokedex.mad.spotless")

    // Compiles src/main/proto/*.proto and generates Java + Kotlin **lite** sources.
    // Version comes from libs.versions.toml → [plugins] protobuf-plugin
    alias(libs.plugins.protobuf.plugin)
}

android {
    // R class + manifest merge package (use your app’s base package, not the reference’s)
    namespace = "ashraf.pokedex.mad.core.datastore"

    defaultConfig {
        // Rules packaged into the AAR for apps that minify; add protobuf/DataStore
        // keep rules here later if R8 strips generated code (often not needed for lite).
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    // Coroutine dispatchers / qualifiers if PreferencesDataSource uses @Dispatcher(IO)
    implementation(projects.core.common)

    // UserData, UiTheme — map proto → domain in PreferencesDataSource
    implementation(projects.core.model)

    // Generic DataStore<T> API for Proto DataStore (T = generated UserPreferences, etc.)
    // `api`: expose to modules that depend on :core:datastore (optional; use `implementation`
    // if you keep DataStore types fully internal).
    api(libs.androidx.dataStore)

    // Kotlin protobuf **lite** runtime — required for generated Kotlin lite code + builders
    implementation(libs.protobuf.kotlin.lite)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

// -----------------------------------------------------------------------------
// Protobuf Gradle plugin: which protoc binary to run + how to generate code
// -----------------------------------------------------------------------------
protobuf {
    // Download/run this protoc version (must match [libraries] protobuf-protoc in catalog)
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    // For every proto generation task: emit Java + Kotlin with **lite** runtime (smaller APK)
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}
