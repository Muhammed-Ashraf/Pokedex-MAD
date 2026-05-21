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

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class StartupBenchmarks {

  // Macrobenchmark harness that repeatedly launches/measures the app.
  @get:Rule
  val rule = MacrobenchmarkRule()

  // Control case: startup without baseline profile optimization.
  @Test
  fun startupCompilationNone() = benchmark(CompilationMode.None())

  // Optimized case: startup with baseline profiles required.
  @Test
  fun startupCompilationBaselineProfiles() =
    benchmark(CompilationMode.Partial(BaselineProfileMode.Require))

  private fun benchmark(compilationMode: CompilationMode) {
    rule.measureRepeated(
      // targetAppId is injected from baselineprofile/build.gradle.kts (androidComponents block).
      packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
        ?: throw IllegalStateException("targetAppId not passed as instrumentation runner arg"),
      // Measure startup timing metrics (time-to-initial-draw related startup signal).
      metrics = listOf(StartupTimingMetric()),
      // Switches between control vs optimized compilation modes above.
      compilationMode = compilationMode,
      // Cold start gives the most realistic "first app launch" comparison.
      startupMode = StartupMode.COLD,
      // Multiple iterations reduce noise and make comparisons more reliable.
      iterations = 10,
      setupBlock = {
        // Reset to launcher before each run.
        pressHome()
      },
      measureBlock = {
        // Launch app and wait until first stable frame/activity state.
        startActivityAndWait()
        // Run key user journey (home list + details) for realistic coverage.
        pokedexScenarios()
      },
    )
  }
}
