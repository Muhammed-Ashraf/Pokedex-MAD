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
package ashraf.pokedex.mad.baselineprofile

import android.annotation.SuppressLint
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates baseline profile rules for the app's startup + key navigation flow.
 *
 * Output is consumed by :app during generateReleaseBaselineProfile and written to:
 * app/src/release/generated/baselineProfiles/
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

  @SuppressLint("NewApi")
  @get:Rule
  val rule = BaselineProfileRule()

  @SuppressLint("NewApi")
  @Test
  fun generate() {
    // applicationId is injected from baselineprofile/build.gradle.kts via instrumentation args.
    rule.collect(
      packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
        ?: throw IllegalStateException("targetAppId not passed as instrumentation runner arg"),
      // Include startup classes/methods in startup profile for better cold start optimization.
      includeInStartupProfile = true,
      // Default max iteration count balances stability and generation time.
      stableIterations = 2,
      maxIterations = 8,
    ) {
      // Cold start path
      pressHome()
      startActivityAndWait()

      // Additional critical app journey (home list + details)
      pokedexScenarios()
    }
  }
}
