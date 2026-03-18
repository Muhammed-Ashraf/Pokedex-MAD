import ashraf.pokedex.mad.configureKotlinAndroid
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Convention plugin for **application** modules (e.g. the app module).
 *
 * Why this exists (vs AndroidLibraryConventionPlugin):
 * - The app module must apply the **application plugin**: "com.android.application",
 *   not the library plugin "com.android.library".
 * - Application modules have app-specific needs:
 *   - applicationId, signing, packaging, launcher activity
 *   - buildTypes that produce APK/AAB
 * - Library modules do NOT have those, they only produce AARs.
 *
 * This plugin:
 * - Applies "com.android.application" + "org.jetbrains.kotlin.android".
 * - Reuses the same shared Kotlin/Android config helpers as the library plugin
 *   (compileSdk, minSdk, Java 17, Kotlin options).
 * - Sets app-specific pieces like targetSdk in one place.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            // Use the Android **application** plugin for app modules.
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")

            // Configure the Android application extension using the same
            // helper you already use for libraries (compileSdk, minSdk, etc.).
            val androidExtension = extensions.getByType<ApplicationExtension>()
            configureKotlinAndroid(androidExtension)

            androidExtension.defaultConfig {
                // Library plugin sets minSdk; here we can also centralize targetSdk
                // for all application modules.
                targetSdk = 36
            }

            // Configure Kotlin Android extension (JVM target, compiler options)
            // exactly like in the library convention plugin.
            extensions.getByType<KotlinAndroidProjectExtension>().apply {
                configureKotlinAndroid(this)
            }
        }
    }
}