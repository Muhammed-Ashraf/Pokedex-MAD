// =============================================================================
// CONVENTION PLUGIN PROJECT
// =============================================================================
// This subproject builds the convention plugins (e.g. Android library plugin).
// Plugins are written in Kotlin under src/main/kotlin/ and get compiled into
// JARs that the root project can apply via id("ashraf.pokedex.mad.android.library").
// =============================================================================

// Enables writing build logic in Kotlin. Gradle will compile .kt files in
// src/main/kotlin/ and expose them as plugins when we register them in
// the gradlePlugin { } block (added in step 2.4).
plugins {
    `kotlin-dsl`
}

// Maven group for the published plugin. Used if you ever publish this to a repo.
// Not required for included builds; keeps things consistent.
group = "ashraf.pokedex.mad.buildlogic"

// Convention build must run on Java 17 so it matches the rest of the project.
// Toolchain ensures the Kotlin plugin code compiles with the right JDK.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// compileOnly = needed to compile our plugin code, but NOT packaged into the
// plugin JAR. When a module applies our plugin, it already has AGP and Kotlin
// from the root build, so we only need the types (LibraryExtension, etc.) at
// compile time.
// - android.gradlePlugin: so we can use com.android.build.gradle.LibraryExtension,
//   CommonExtension, and other AGP APIs in our Kotlin plugin class.
// - kotlin.gradlePlugin: so we can use KotlinAndroidProjectExtension, JvmTarget,
//   etc. in KotlinAndroid.kt.
dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
}
