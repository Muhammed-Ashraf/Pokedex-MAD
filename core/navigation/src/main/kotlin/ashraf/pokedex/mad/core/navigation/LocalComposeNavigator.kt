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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal that provides the app navigator to the composable tree.
 *
 * Why this exists:
 * - Avoids passing navigator through every composable parameter.
 * - Any composable inside the provider can access navigation actions.
 */
val LocalComposeNavigator: ProvidableCompositionLocal<PokedexNavigator> =
  compositionLocalOf {
    error(
      "No PokedexNavigator provided! " +
        "Make sure to provide a navigator at the app/root navigation host level.",
    )
  }

/**
 * Convenience accessor for the current navigator from CompositionLocal.
 *
 * Usage from any composable:
 *   val navigator = currentComposeNavigator
 *   navigator.navigate(PokedexScreen.Settings)
 */
val currentComposeNavigator: PokedexNavigator
  @Composable
  @ReadOnlyComposable
  get() = LocalComposeNavigator.current
