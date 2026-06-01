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
    // Base Android setup for library modules (compileSdk/minSdk/Java/Kotlin defaults, etc.)
    id("ashraf.pokedex.mad.android.library")

    // Enables Jetpack Compose for this *library* module (buildFeatures + compose plugin).
    // Compose enabled via the library-compose convention plugin.
    id("ashraf.pokedex.mad.android.library.compose")

    // Hilt in one line (applies hilt + ksp + adds hilt dependencies).
    // Stable marker for Compose stability analysis in this module.
    id("ashraf.pokedex.mad.android.hilt")

    // Shared formatting + license headers.
    id("ashraf.pokedex.mad.spotless")
}

android {
    // Namespace for this module (controls the R class + generated resources)
    // Use your core module namespace so it’s consistent.
    namespace = "ashraf.pokedex.mad.core.preview"
}

dependencies {
    // ----- core modules -----

    // Theme / colors / typography tokens used by Preview wrappers.
    // Purpose: previews should render with the correct app look.
    implementation(projects.core.designsystem)

    // Navigation contracts/types used by preview screens (if any).
    // Purpose: preview can build composables that depend on navigation types.
    implementation(projects.core.navigation)

    // Domain models used by preview UI state/samples.
    implementation(projects.core.model)
    
}