// =============================================================================
// CONVENTION PLUGIN PROJECT
// =============================================================================
// This subproject builds the convention plugins (e.g. Android library plugin).
// Plugins are written in Kotlin under src/main/kotlin/ and get compiled into
// JARs that the root project can apply via id("ashraf.pokedex.mad.android.library").
// =============================================================================

// Enables writing build logic in Kotlin. Gradle compiles .kt files in
// src/main/kotlin/ and exposes them as plugins via the gradlePlugin { } block below.
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
// -----------------------------------------------------------------------------
// Step 2.4: Register the convention plugin so the root project can apply it.
// When a module uses id("ashraf.pokedex.mad.android.library"), Gradle loads
// this included build, finds the plugin by id, and runs AndroidLibraryConventionPlugin.apply(project).
// "androidLibrary" is the registration name (used only inside this file);
// the important part is "id" (what modules write) and "implementationClass" (the class to run).
// -----------------------------------------------------------------------------
gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "ashraf.pokedex.mad.android.library"
            implementationClass = "ashraf.pokedex.mad.AndroidLibraryConventionPlugin"
        }
        // Phase 2.6: Hilt in one id — applies Hilt + KSP and adds hilt-android, hilt-navigation-compose, ksp(hilt-compiler).
        register("androidHilt") {
            id = "ashraf.pokedex.mad.android.hilt"
            implementationClass = "ashraf.pokedex.mad.AndroidHiltConventionPlugin"
        }
        // Phase 2.6: Spotless in one id — applies Spotless and configures ktlint + license headers for .kt, .kts, .xml.
        register("spotless") {
            id = "ashraf.pokedex.mad.spotless"
            implementationClass = "ashraf.pokedex.mad.SpotlessConventionPlugin"
        }
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
    // Spotless: must be implementation (not compileOnly) so SpotlessExtension is on the plugin's runtime
    // classpath. Our SpotlessConventionPlugin calls extensions.configure<SpotlessExtension>; the classloader
    // that loaded the plugin must be able to load SpotlessExtension. With compileOnly, Spotless is only
    // available at compile time, so at sync you get "Unable to load class SpotlessExtension".
    // The reference project (pokedex-compose) may use compileOnly and still work: different Gradle versions
    // handle included-build classloading differently (newer Gradle isolates more, so compileOnly is not enough).
    // Using implementation here is the reliable fix; behavior (formatting, license headers) is the same.
    implementation(libs.spotless.gradlePlugin)
}
