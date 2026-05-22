/*
 * Designed and developed for Pokedex-MAD (learning project)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
    // Base Android library conventions:
    // compileSdk/minSdk/Java/Kotlin defaults for all library modules.
    id("ashraf.pokedex.mad.android.library")

    // Enables Compose for library modules (buildFeatures.compose + compose plugin).
    id("ashraf.pokedex.mad.android.library.compose")

    // Needed for serializable route objects / payloads in navigation.
    alias(libs.plugins.kotlinx.serialization)

    // Hilt for navigator bindings used with Compose.
    // Useful when navigation-related classes need DI.
    id("ashraf.pokedex.mad.android.hilt")

    // Formatting + license headers consistency.
    id("ashraf.pokedex.mad.spotless")
}

android {
    // Package namespace for this module.
    namespace = "ashraf.pokedex.mad.core.navigation"
}

dependencies {
    // Shared domain models (used by typed routes / nav state if needed).
    implementation(projects.core.model)

    // Basic Android + coroutine support used by navigation classes/composables.
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    // Navigation3 libs exposed as API.
    // Why `api` (not implementation):
    // - Modules that depend on core:navigation can use navigation types directly
    //   without re-declaring these dependencies.
    // - Navigation 3 UI dependency for composable transitions.
    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.navigation3.ui)

    // JSON serialization support for route arguments/state objects.
    implementation(libs.kotlinx.serialization.json)
}