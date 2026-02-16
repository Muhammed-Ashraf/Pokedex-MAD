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
// "internal" = only visible inside this build-logic project, not to the root build.
// =============================================================================

/**
 * Configures the Android block (compileSdk, minSdk, Java version, lint).
 * Receives CommonExtension because both Application and Library use it;
 * our convention plugin passes the LibraryExtension (which extends CommonExtension).
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        // API level we compile against. Must be >= targetSdk. 36 = latest at time of setup.
        compileSdk = 36

        defaultConfig {
            // Minimum Android version the library supports. Match your app's minSdk.
            minSdk = 29
        }

        // Java language level for the Android compilation. 17 is required for recent AGP/Kotlin.
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        // Don't fail the build on lint errors; library modules often inherit strict lint from app.
        lint {
            abortOnError = false
        }
    }
}

/**
 * Configures the Kotlin compiler (JVM target and compiler arguments).
 * Keeps Kotlin bytecode and compiler opts consistent across all library modules.
 */
internal fun Project.configureKotlinAndroid(
    extension: KotlinAndroidProjectExtension,
) {
    extension.apply {
        compilerOptions {
            // Emit bytecode for JVM 17 so it matches compileOptions above.
            jvmTarget.set(JvmTarget.JVM_17)

            // Opt-ins: allow using experimental APIs without annotating every call site.
            // Add/remove as your app needs (e.g. Compose, Coroutines, Navigation).
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
