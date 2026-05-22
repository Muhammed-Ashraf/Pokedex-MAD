package ashraf.pokedex.mad  // keep same as existing

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * Configure Compose-specific options for Android modules.
 *
 * Shared Compose setup for library/application modules:
 * - Applies the Kotlin Compose plugin.
 * - Enables buildFeatures.compose.
 * - Configures Compose compiler reports output directory.
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension,
) {
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    commonExtension.buildFeatures.compose = true

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
    }
}