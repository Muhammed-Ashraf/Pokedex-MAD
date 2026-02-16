// =============================================================================
// BUILD-LOGIC SETTINGS
// =============================================================================
// This file configures the "included build" at build-logic/. When the root
// project runs includeBuild("build-logic"), Gradle runs this settings file first.
// It defines where this build can get dependencies and which subprojects it has.
// =============================================================================

// Where the build-logic build can download dependencies (AGP, Kotlin plugin, etc.).
// Without this, the convention subproject could not resolve compileOnly(...) deps.
// versionCatalogs must be INSIDE this block in included builds (Settings doesn't
// expose top-level versionCatalogs here), so we reuse the root's libs from one place.
dependencyResolutionManagement {
    repositories {
        google()   // Android Gradle Plugin, AndroidX
        mavenCentral()  // Kotlin Gradle Plugin
    }
    // Reuse the ROOT project's version catalog so convention/build.gradle.kts can
    // use libs.android.gradlePlugin and libs.kotlin.gradlePlugin. Path is relative
    // to build-logic/ → "../gradle/libs.versions.toml" = root's version catalog.
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

// Declare the only subproject of this included build: convention.
// Gradle will look for build-logic/convention/build.gradle.kts and compile
// the Kotlin plugin code we put in convention/src/main/kotlin/.
include(":convention")
