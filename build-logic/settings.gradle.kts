// =============================================================================
// BUILD-LOGIC SETTINGS
// =============================================================================
// This file configures the "included build" at build-logic/. When the root
// project runs includeBuild("build-logic"), Gradle runs this settings file first.
// It defines where this build can get dependencies and which subprojects it has.
// =============================================================================

// Where the build-logic build can download dependencies (AGP, Kotlin plugin, etc.).
// Without this, the convention subproject could not resolve compileOnly(...) deps.
dependencyResolutionManagement {
    repositories {
        google()   // Android Gradle Plugin, AndroidX
        mavenCentral()  // Kotlin Gradle Plugin
    }
}

// Reuse the ROOT project's version catalog so we can say libs.android.gradlePlugin
// instead of hardcoding coordinates. "../gradle/libs.versions.toml" is relative
// to the build-logic/ folder, so it points to the root's gradle/libs.versions.toml.
// One catalog = one place to bump AGP/Kotlin versions for the whole project.
versionCatalogs {
    create("libs") {
        from(files("../gradle/libs.versions.toml"))
    }
}

// Declare the only subproject of this included build: convention.
// Gradle will look for build-logic/convention/build.gradle.kts and compile
// the Kotlin plugin code we put in convention/src/main/kotlin/.
include(":convention")
