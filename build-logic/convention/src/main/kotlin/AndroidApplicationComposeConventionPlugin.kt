import ashraf.pokedex.mad.configureAndroidCompose
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

/**
 * Application Compose convention plugin.
 *
 * - Applies the Android application plugin (defensive).
 * - Applies the Compose stability analyzer plugin.
 * - Delegates actual Compose config to configureAndroidCompose.
 */
class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("com.github.skydoves.compose.stability.analyzer")
            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)
        }
    }
}