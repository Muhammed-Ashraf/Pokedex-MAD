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
package ashraf.pokedex.mad.core.viewmodel

import androidx.lifecycle.ViewModel

/**
 * Base class for ViewModels in this reference-style architecture.
 *
 * Purpose:
 * - Provides a stable [ViewModelKey] for the ViewModel type.
 * - Provides helper to create a keyed [ViewModelStateFlow].
 *
 * Why it’s useful:
 * - It connects the ViewModel type -> [ViewModelKey] -> [ViewModelStateFlow] scoping.
 * - Helps keep state mutation safe and predictable.
 */
abstract class BaseViewModel : ViewModel() {

  /**
   * The key is derived from this ViewModel's class name.
   * That means different ViewModel types get different keys.
   */
  protected val key: ViewModelKey = ViewModelKey(this::class.java.name)

  /**
   * Helper to create a keyed [ViewModelStateFlow].
   *
   * Usage:
   *   private val state = viewModelStateFlow(InitialUiState(...))
   */
  protected fun <T> viewModelStateFlow(value: T): ViewModelStateFlow<T> {
    return ViewModelStateFlow(key = key, value = value)
  }
}
