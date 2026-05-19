// Enables type-safe accessors: projects.core.model instead of project(":core:model")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    // build-logic included build so we can use our convention plugins by id.
    includeBuild("build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("androidx\\..*")
                includeGroupByRegex("com\\.android(\\..*|)")
                includeGroupByRegex("com\\.google\\.android\\..*")
                includeGroupByRegex("com\\.google\\.firebase(\\..*|)")
                includeGroupByRegex("com\\.google\\.gms(\\..*|)")
                includeGroupByRegex("com\\.google\\.mlkit")
                includeGroupByRegex("com\\.google\\.oboe")
                includeGroupByRegex("com\\.google\\.prefab")
                includeGroupByRegex("com\\.google\\.testing\\.platform")
            }
            mavenContent {
                releasesOnly()
            }
        }
        // fetch plugins
        mavenCentral() {
            content {
                includeGroup("com.google.dagger")
                includeGroup("com.google.dagger.hilt.android")
                includeGroup("com.github.skydoves.compose.stability.analyzer")
            }
            mavenContent {
                releasesOnly()
            }
        }

        // Gradle Plugin Portal:
        // This is where Gradle looks up third‑party Gradle plugins by id,
        // for example:
        //  - com.github.skydoves.compose.stability.analyzer
        //  - org.jetbrains.kotlin.jvm / android / serialization (when applied via id)
        //
        // Because this is present, we can apply plugins like
        // "com.github.skydoves.compose.stability.analyzer" in build-logic
        // and Gradle will be able to download/resolve them.
        gradlePluginPortal()

        // fetch snapshot plugins from sonatype
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // fetch libraries from google maven (https://maven.google.com)
        google() {
            content {
                includeGroupByRegex("androidx\\..*")
                includeGroupByRegex("com\\.android(\\..*|)")
                includeGroupByRegex("com\\.google\\.android\\..*")
                includeGroupByRegex("com\\.google\\.firebase(\\..*|)")
                includeGroupByRegex("com\\.google\\.gms(\\..*|)")
                includeGroupByRegex("com\\.google\\.mlkit")
                includeGroupByRegex("com\\.google\\.oboe")
                includeGroupByRegex("com\\.google\\.prefab")
                includeGroupByRegex("com\\.google\\.testing\\.platform")
            }
            mavenContent {
                releasesOnly()
            }
        }

        // fetch libraries from maven central
        mavenCentral() {
            mavenContent {
                releasesOnly()
            }
        }

        // fetch snapshot libraries from sonatype
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}

rootProject.name = "Pokedex-MAD"
include(":app")
include(":core:model")
include(":core:network")
include(":core:database")
include(":core:data")
include(":core:common")
include(":core:designsystem")
include(":core:navigation")
include(":core:viewmodel")
include(":core:preview")
include(":core:datastore")
include(":core:test")

include(":feature:home")
include(":feature:settings")

include(":baselineprofile")
