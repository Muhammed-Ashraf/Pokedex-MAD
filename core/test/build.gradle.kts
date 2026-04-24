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
    id("ashraf.pokedex.mad.android.library")
    id("ashraf.pokedex.mad.spotless")
}
android { namespace = "ashraf.pokedex.mad.core.test" }

dependencies {
    // Domain types (Pokemon, UserData, etc.) used by test fakes and MockUtil helpers.
    // compileOnly would hide symbols from test sources; implementation is correct for a shared test module.
    implementation(projects.core.model)

    // Main dispatcher on Android; keeps behavior aligned with app modules that use Dispatchers.Main.
    // MainCoroutinesRule uses setMain(...) from the test artifact; this dependency matches the reference setup.
    implementation(libs.kotlinx.coroutines.android)

    // TestCoroutineScheduler, runTest, TestDispatcher, Dispatchers.setMain / resetMain for JVM/unit tests.
    implementation(libs.kotlinx.coroutines.test)

    // JUnit 4 rules (e.g. @get:Rule MainCoroutinesRule : TestWatcher) and @Test annotations used by core:* tests.
    implementation(libs.junit)
}