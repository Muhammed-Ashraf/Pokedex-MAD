package ashraf.pokedex.mad

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin: code formatting and license headers in one id.
 *
 * **Concept — Spotless:** Spotless is a Gradle plugin that formats source files (Kotlin, XML,
 * etc.) and can prepend a license header. By putting the configuration here, every module
 * that applies `id("ashraf.pokedex.mad.spotless")` gets the same rules without copying a long
 * spotless { } block. Run `./gradlew spotlessApply` to fix formatting; use `spotlessCheck` in CI.
 *
 * **Why this exists:** Matches the reference project (pokedex-compose), which uses a single
 * spotless convention plugin. Ensures consistent style and license headers across app and
 * all core/feature modules; one place to change indent size or license text.
 *
 * **What this plugin does:**
 * 1. Applies the Spotless Gradle plugin.
 * 2. Configures three formats (Kotlin, .kts, XML) with the same rules: exclude build output,
 *    add license header from root spotless/ files, trim trailing whitespace, end with newline.
 * 3. For Kotlin, uses ktlint with overrides so Composable functions can use lowercase names.
 */
class SpotlessConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.diffplug.spotless")

            extensions.configure<SpotlessExtension> {
                // Do not format generated or build output; only source.
                val buildDirectory = layout.buildDirectory.asFileTree

                // ---- Kotlin (.kt) ----
                kotlin {
                    target("**/*.kt")
                    targetExclude(buildDirectory)
                    // ktlint: Kotlin linter/formatter. Overrides: 2-space indent; allow lowercase
                    // function names when annotated with @Composable (Compose convention).
                    ktlint().editorConfigOverride(
                        mapOf(
                            "indent_size" to "2",
                            "continuation_indent_size" to "2",
                            "ktlint_function_naming_ignore_when_annotated_with" to "Composable"
                        )
                    )
                    // Prepend this license block to every .kt file (from project root).
                    licenseHeaderFile(rootProject.file("spotless/spotless.license.kt"))
                    trimTrailingWhitespace()
                    endWithNewline()
                }

                // ---- Gradle Kotlin DSL (.kts) ----
                format("kts") {
                    target("**/*.kts")
                    targetExclude(buildDirectory)
                    // Regex: where to insert the license (before first line that isn't a comment).
                    licenseHeaderFile(rootProject.file("spotless/spotless.license.kt"), "(^(?![\\/ ]\\*).*$)")
                }

                // ---- XML (manifests, layouts, etc.) ----
                format("xml") {
                    target("**/*.xml")
                    targetExclude(buildDirectory)
                    // Insert license before first tag (e.g. <manifest or <layout).
                    licenseHeaderFile(rootProject.file("spotless/spotless.license.xml"), "(<[^!?])")
                }
            }
        }
    }
}
