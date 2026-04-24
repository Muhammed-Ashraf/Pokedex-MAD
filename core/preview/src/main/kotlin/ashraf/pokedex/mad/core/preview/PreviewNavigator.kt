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
package ashraf.pokedex.mad.core.preview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import ashraf.pokedex.mad.core.designsystem.theme.PokedexTheme
import ashraf.pokedex.mad.core.navigation.LocalComposeNavigator
import ashraf.pokedex.mad.core.navigation.PokedexNavigator
import ashraf.pokedex.mad.core.navigation.PokedexScreen

/**
 * No-op navigator for previews.
 *
 * Why: real screens call [currentComposeNavigator] / [LocalComposeNavigator]. Outside the app
 * NavHost there is no [NavBackStack]. Providing this avoids crashes and keeps previews runnable.
 */
private object PreviewNavigator : PokedexNavigator {
  override fun navigate(screen: PokedexScreen) = Unit
  override fun navigateUp(): Boolean = false
}

/**
 * Wraps preview content with the same “tree” real UI expects:
 * - [LocalComposeNavigator] so navigation calls don’t crash.
 * - [PokedexTheme] so colors/background match the app.
 * - [SharedTransitionLayout] + [AnimatedContent] so composables that take `SharedTransitionScope` /
 *   `AnimatedContentScope` (e.g. home/detail shared element APIs) compile and preview like in [PokedexNavHost].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PokedexPreviewTheme(
  content: @Composable androidx.compose.animation.SharedTransitionScope.(
    androidx.compose.animation.AnimatedContentScope,
  ) -> Unit,
) {
  CompositionLocalProvider(
    LocalComposeNavigator provides PreviewNavigator,
  ) {
    PokedexTheme {
      SharedTransitionLayout {
        AnimatedContent(targetState = Unit, label = "") {
          content(this@SharedTransitionLayout, this)
        }
      }
    }
  }
}
