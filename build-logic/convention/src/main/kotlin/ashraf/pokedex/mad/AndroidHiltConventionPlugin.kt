package ashraf.pokedex.mad

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * Convention plugin: "Hilt for Android" in one id.
 *
 * **Concept — Convention plugins:** Instead of every module that needs Hilt repeating the same
 * plugin applications and dependencies, we bundle them here. When a module applies
 * `id("ashraf.pokedex.mad.android.hilt")`, Gradle runs this class once for that project.
 *
 * **Why this exists:** The reference project (pokedex-compose) uses a single id like
 * `skydoves.pokedex.android.hilt` per module. We mirror that so app, core:network, and later
 * core:database and feature modules only add one line instead of Hilt plugin + KSP plugin +
 * implementation(hilt-android) + implementation(hilt-navigation-compose) + ksp(hilt-compiler).
 * Version and library choices stay in the version catalog; this plugin just applies them.
 *
 * **What this plugin does:**
 * 1. Applies the official Hilt Android Gradle plugin (enables @AndroidEntryPoint, @HiltViewModel, etc.).
 * 2. Applies the KSP plugin (required for Hilt's annotation processor to run).
 * 3. Adds the three dependencies below from the root project's version catalog (libs).
 */
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.dagger.hilt.android")
                apply("com.google.devtools.ksp")
            }

            // Version catalog is provided by the root build; convention build has access via
            // versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
            // in build-logic/settings.gradle.kts. "libs" is the name used in the root.
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                // Runtime + annotations for Hilt on Android (e.g. @Inject, @AndroidEntryPoint).
                add("implementation", libs.findLibrary("hilt.android").get())
                // Integration with Jetpack Navigation Compose: hiltViewModel(), etc.
                add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
                // KSP processor that generates DI code from Hilt annotations.
                add("ksp", libs.findLibrary("hilt.compiler").get())
            }
        }
    }
}
