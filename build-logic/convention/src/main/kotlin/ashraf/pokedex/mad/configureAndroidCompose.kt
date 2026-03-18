package ashraf.pokedex.mad  // keep same as existing

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * Configure Compose-specific options for Android modules.
 *
 * Mirrors the reference project:
 * - Applies the Kotlin Compose plugin.
 * - Enables buildFeatures.compose.
 * - Configures Compose compiler reports output directory.
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    // Kotlin Compose plugin (same as reference).
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    commonExtension.apply {
        buildFeatures {
            compose = true
        }
    }

//    // Configure Compose compiler plugin (reports, etc.).
//    extensions.configure<ComposeCompilerGradlePluginExtension> {
//        reportsDestination = layout.buildDirectory.dir("compose_compiler")
//    }
}