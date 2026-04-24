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
package ashraf.pokedex.mad.core.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit [TestWatcher] that swaps [Dispatchers.Main] for a [TestDispatcher] for each test.
 *
 * Why:
 * - Production code often uses `Dispatchers.Main` (or patterns tied to it).
 * - In unit tests there is no real Android main looper; without this, those calls are wrong or flaky.
 *
 * Lifecycle:
 * - [starting]: [Dispatchers.setMain] so `Main` points at [testDispatcher] for the duration of one test.
 * - [finished]: [Dispatchers.resetMain] so the next test does not inherit a stale dispatcher.
 *
 * Dispatcher choice:
 * - [UnconfinedTestDispatcher] runs coroutines eagerly in tests (simple, deterministic for many cases).
 * - Other tests may use [kotlinx.coroutines.test.StandardTestDispatcher] when you need manual time control.
 *
 * [testScope]:
 * - Not used *inside* this class; it is a **public helper** for tests: `rule.testScope.runTest { ... }`
 *   uses the **same** dispatcher as [setMain], so under-test code and test coroutines stay aligned.
 *
 * [super.starting]:
 * - [TestWatcher]'s default [starting] is effectively a no-op; omitting `super.starting` is normal.
 * - You may call `super.starting(description)` for symmetry with [finished]; behavior is usually unchanged.
 */
class MainCoroutinesRule(
  val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
  // Exposed for tests: runTest { } on the same dispatcher as Dispatchers.Main during this @Test.
  val testScope = TestScope(testDispatcher)

  override fun starting(description: Description?) {
    // Replace Main with the test dispatcher for this test only.
    Dispatchers.setMain(testDispatcher)
  }

  override fun finished(description: Description?) {
    super.finished(description)
    // Restore the real Main dispatcher after this test.
    Dispatchers.resetMain()
  }
}
