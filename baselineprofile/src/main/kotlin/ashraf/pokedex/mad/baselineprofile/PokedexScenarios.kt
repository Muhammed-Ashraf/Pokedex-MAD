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

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

/**
 * Defines the critical user journey used by:
 * - Baseline profile generation
 * - Startup macrobenchmark measurement
 *
 * This flow mirrors the reference project:
 * Home visible -> list interaction -> navigate to details -> details visible.
 */
fun MacrobenchmarkScope.pokedexScenarios() {
  // -----------------
  // Home journey
  // -----------------
  explorePokedexHome()
  navigateFromHomeToDetails()

  // -----------------
  // Details journey
  // -----------------
  detailsWaitForContent()
}

/**
 * Wait until Home content is present, then perform one down/up fling.
 * This ensures list-related code paths are exercised.
 */
fun MacrobenchmarkScope.explorePokedexHome() = device.apply {
  homeWaitForContent()
  pokedexListScrollDownUp()
}

/**
 * Waits for the home list composable.
 *
 * Why By.res("PokedexList") works:
 * Your UI sets Modifier.testTag("PokedexList"), and theme enables
 * testTagsAsResourceId = true, so UIAutomator can query it as a resource id.
 */
fun MacrobenchmarkScope.homeWaitForContent() = device.apply {
  wait(Until.hasObject(By.res("PokedexList")), 15_000L)
}

/**
 * Finds the list and flings down then up to simulate basic interaction.
 */
fun MacrobenchmarkScope.pokedexListScrollDownUp() = device.apply {
  val pokedexList = waitAndFindObject(By.res("PokedexList"), 15_000L)
  flingElementDownUp(pokedexList)
}

/**
 * Taps one Pokemon card from the Home list to navigate to details.
 *
 * Uses testTag("Pokemon") from the card composable.
 */
fun MacrobenchmarkScope.navigateFromHomeToDetails() = device.apply {
  waitAndFindObject(By.res("Pokemon"), 15_000L).click()
  waitForIdle()
}

/**
 * Waits for details screen root node tagged as "PokedexDetails".
 * This confirms navigation + initial details content rendering.
 */
fun MacrobenchmarkScope.detailsWaitForContent() = device.apply {
  wait(Until.hasObject(By.res("PokedexDetails")), 15_000L)
}

/**
 * Performs a down/up fling with safe gesture margins to avoid triggering
 * system back/home edge gestures on modern devices.
 */
internal fun UiDevice.flingElementDownUp(element: UiObject2) {
  element.setGestureMargin(displayWidth / 5)

  element.fling(Direction.DOWN)
  waitForIdle()
  element.fling(Direction.UP)
}

/**
 * Waits for [selector] and returns the matching object.
 *
 * Why helper exists:
 * - Avoids flaky "element not found" failures
 * - Provides a clear assertion error with timeout details
 */
internal fun UiDevice.waitAndFindObject(
  selector: BySelector,
  timeout: Long = 15_000L,
): UiObject2 {
  if (!wait(Until.hasObject(selector), timeout)) {
    throw AssertionError("Element not found in ${timeout}ms (selector=$selector)")
  }
  return findObject(selector)
}
