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
package ashraf.pokedex.mad.core.navigation

import androidx.navigation3.runtime.NavKey
import ashraf.pokedex.mad.core.model.Pokemon
import kotlinx.serialization.Serializable

/**
 * Typed destinations for Navigation3.
 *
 * Why typed screens (instead of raw string routes):
 * - Compile-time safety for destination arguments.
 * - Fewer route string mistakes/typos.
 * - Better refactoring support.
 *
 * Nav3 expects destinations to be NavKey-compatible and serializable.
 */
sealed interface PokedexScreen : NavKey {

  /**
   * Home list screen (start destination).
   */
  @Serializable
  data object Home : PokedexScreen

  /**
   * Detail screen carrying a selected Pokemon.
   *
   * Reference mirrors this by passing a model object directly.
   * (Later you may optimize to pass only an id/name and load details from repository.)
   */
  @Serializable
  data class Details(val pokemon: Pokemon) : PokedexScreen

  /**
   * Settings screen destination.
   */
  @Serializable
  data object Settings : PokedexScreen
}
