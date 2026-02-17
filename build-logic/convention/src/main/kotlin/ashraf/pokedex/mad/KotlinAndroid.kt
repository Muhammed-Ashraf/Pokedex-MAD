package ashraf.pokedex.mad

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

// =============================================================================
// SHARED KOTLIN + ANDROID CONFIG (Step 2.3)
// =============================================================================
// These functions are called by our convention plugin (AndroidLibraryConventionPlugin)
// so every Android library module gets the same compileSdk, minSdk, Java 17, and
// Kotlin options without repeating them in each module's build.gradle.kts.
// Uses CommonExtension (AGP 8.x) so the same config works for library (and app if needed).
// "internal" = only visible inside this build-logic project, not to the root build.
// =============================================================================

/**
 * Configures the Android block (compileSdk, minSdk, Java version, lint).
 * CommonExtension is the base for both Application and Library in AGP 8.x.
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = 36

        defaultConfig {
            minSdk = 29
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        lint {
            abortOnError = false
        }
    }
}

/**
 * Configures the Kotlin compiler (JVM target and compiler arguments).
 */
internal fun Project.configureKotlinAndroid(
    extension: KotlinAndroidProjectExtension,
) {
    extension.apply {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)

            freeCompilerArgs.set(
                freeCompilerArgs.getOrElse(emptyList()) + listOf(
                    "-Xopt-in=kotlin.RequiresOptIn",
                    "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                    "-Xopt-in=androidx.lifecycle.compose.ExperimentalLifecycleComposeApi",
                ),
            )
        }
    }
}
