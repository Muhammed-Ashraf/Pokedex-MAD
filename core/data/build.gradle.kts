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
    // Applies shared Android library defaults (compileSdk/minSdk/Java 17/Kotlin options).
    id("ashraf.pokedex.mad.android.library")

    // Applies Hilt + KSP + Hilt deps via our convention plugin.
    // Needed because core:data will provide/bind repositories for injection.
    id("ashraf.pokedex.mad.android.hilt")

    // Applies Spotless formatting + license headers consistently across modules.
    id("ashraf.pokedex.mad.spotless")
}

android {
    // Unique package namespace for this module's R class + manifest merging.
    namespace = "ashraf.pokedex.mad.core.data"
}

dependencies {
    // Domain models shared across layers (Pokemon, etc.)
    api(projects.core.model)

    // kotlinx
    api(libs.kotlinx.immutable.collection)

    // Network layer (Retrofit + service + Sandwich wrappers).
    // core:data will call this and map results into domain models.
    implementation(projects.core.network)

    // Database layer (Room DAO + entities + mappers).
    // core:data will use this for caching/offline-first behavior.
    implementation(projects.core.database)

    implementation(projects.core.datastore)

    implementation(projects.core.common)

    // Repository implementations use coroutines/Flow.
    implementation(libs.kotlinx.coroutines.android)

    // Sandwich: handy operators for handling API responses (Success / Error).
    implementation(libs.sandwich)

    testImplementation(projects.core.test)
    testImplementation(libs.kotlinx.coroutines.test)
    // unit test
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.protobuf.kotlin.lite)
}