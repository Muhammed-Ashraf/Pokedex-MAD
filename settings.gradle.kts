// Enables type-safe accessors: projects.core.model instead of project(":core:model")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    // build-logic included build so we can use our convention plugins by id.
    includeBuild("build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()

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
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
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

include(":feature:home")
