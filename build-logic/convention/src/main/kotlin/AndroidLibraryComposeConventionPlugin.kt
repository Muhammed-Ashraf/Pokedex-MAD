import ashraf.pokedex.mad.configureAndroidCompose
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * Convention plugin to enable Jetpack Compose for Android **library** modules.
 *
 * Purpose (reference-aligned):
 * - Keeps library modules minimal (no buildFeatures.compose = true in each module).
 * - Centralizes Compose enablement + Kotlin compose plugin application.
 * - Used by UI libraries like core:designsystem (and later feature UI modules).
 */
class AndroidLibraryComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            // Ensure the library plugin is applied (defensive).
            pluginManager.apply("com.android.library")

            // Apply Kotlin Compose plugin (same id used by your catalog: org.jetbrains.kotlin.plugin.compose).
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val extension = extensions.getByType<com.android.build.gradle.LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }
}