package ashraf.pokedex.mad

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

// =============================================================================
// ANDROID LIBRARY CONVENTION PLUGIN (Step 2.4)
// =============================================================================
// When a module applies id("ashraf.pokedex.mad.android.library"), Gradle
// instantiates this class and calls apply(project). We apply the official
// Android Library and Kotlin Android plugins (AGP 8.x) and configure them
// using the shared KotlinAndroid.kt helpers.
// =============================================================================

/**
 * Convention plugin for Android library modules (e.g. core:model, core:network).
 * Apply it in a module's build.gradle.kts with: id("ashraf.pokedex.mad.android.library")
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 36
            }

            extensions.getByType<KotlinAndroidProjectExtension>().apply {
                configureKotlinAndroid(this)
            }
        }
    }
}
