import ashraf.pokedex.mad.configureKotlinAndroid
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
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
            with(pluginManager) {
                apply("com.android.application")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 36
            }

            extensions.getByType<KotlinAndroidProjectExtension>().apply {
                configureKotlinAndroid(this)
            }
        }
    }
}