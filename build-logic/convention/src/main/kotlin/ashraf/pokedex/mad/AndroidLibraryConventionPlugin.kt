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
// instantiates this class and calls apply(project). We then apply the official
// Android Library and Kotlin Android plugins and configure them using the
// shared KotlinAndroid.kt helpers. Result: the module only needs to set
// namespace and dependencies; compileSdk, minSdk, Java 17, etc. come from here.
// =============================================================================

/**
 * Convention plugin for Android library modules (e.g. core:model, core:network).
 * Apply it in a module's build.gradle.kts with: id("ashraf.pokedex.mad.android.library")
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            // -----------------------------------------------------------------
            // 1. Apply the official plugins so this project is an Android library
            //    with Kotlin. Without these, there would be no android { } or
            //    Kotlin compiler; we only configure what they add.
            // -----------------------------------------------------------------
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            // -----------------------------------------------------------------
            // 2. Configure the Android block (compileSdk, minSdk, Java 17, lint).
            //    LibraryExtension is the android { } type for library projects.
            //    We delegate to KotlinAndroid.kt so all libraries share one config.
            // -----------------------------------------------------------------
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                // Libraries typically match the app's targetSdk so they don't
                // pull in APIs the app can't use.
                defaultConfig.targetSdk = 36
            }

            // -----------------------------------------------------------------
            // 3. Configure the Kotlin block (JVM 17, freeCompilerArgs/opt-ins).
            //    KotlinAndroidProjectExtension is the kotlin { } type. Again we
            //    use the shared helper so every library gets the same Kotlin opts.
            // -----------------------------------------------------------------
            extensions.getByType<KotlinAndroidProjectExtension>().apply {
                configureKotlinAndroid(this)
            }
        }
    }
}
