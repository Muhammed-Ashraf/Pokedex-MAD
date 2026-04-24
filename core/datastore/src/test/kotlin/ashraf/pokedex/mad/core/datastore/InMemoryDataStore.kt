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
package ashraf.pokedex.mad.core.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet

/**
 * In-memory implementation of DataStore.
 *
 * Used mainly for:
 * - Unit tests
 * - Fake repositories
 * - Avoiding disk I/O (no persistence)
 *
 * Data is stored only in RAM using StateFlow.
 */
class InMemoryDataStore<T>(initialValue: T) : DataStore<T> {

  /**
   * Backing storage for the DataStore.
   *
   * MutableStateFlow is used because:
   * - It holds a current value
   * - It emits updates to collectors
   * - It behaves like a reactive data holder
   *
   * So this replaces file-based storage with in-memory storage.
   */
  override val data = MutableStateFlow(initialValue)

  /**
   * Updates the stored value atomically.
   *
   * transform: a function that takes the current value
   * and returns a new value.
   *
   * Example:
   *   current = 10
   *   transform = { it + 5 }
   *   result = 15
   *
   * updateAndGet ensures:
   * - Thread-safe update
   * - Returns the new updated value
   */
  override suspend fun updateData(transform: suspend (T) -> T): T =
    data.updateAndGet { currentValue ->
      // Apply transformation to current value
      transform(currentValue)
    }
}
