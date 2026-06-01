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
package ashraf.pokedex.mad.core.designsystem.theme

/**
 * Immutable palette for the app, loaded from **`res/values/colors.xml`** via [colorResource].
 *
 * **Why XML instead of Kotlin `Color(0xFF…)` only?**
 * - Designers / theming stay in resources; light vs dark uses **different
 *   resource names** (e.g. `background` vs `background_dark`), not only `values-night/`.
 * - Contrasts with the default Studio template, which usually defines purple/pink **Kotlin** colors
 *   and passes them into `lightColorScheme` / `darkColorScheme` inside `MaterialTheme`.
 *
 * Access at runtime through **[PokedexTheme]** (see `PokedexTheme.kt`): `PokedexTheme.colors`.
 */

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import ashraf.pokedex.mad.core.designsystem.R

@Immutable
data class PokedexColors(
  val primary: Color,
  val background: Color,
  val backgroundLight: Color,
  val backgroundDark: Color,
  val absoluteWhite: Color,
  val absoluteBlack: Color,
  val white: Color,
  val white12: Color,
  val white56: Color,
  val white70: Color,
  val black: Color,
  val gray21: Color,
  val bug: Color,
  val dark: Color,
  val dragon: Color,
  val electric: Color,
  val fairy: Color,
  val fire: Color,
  val fighting: Color,
  val flying: Color,
  val ghost: Color,
  val steel: Color,
  val ice: Color,
  val poison: Color,
  val psychic: Color,
  val rock: Color,
  val water: Color,
  val grass: Color,
  val ground: Color,
  val orange: Color,
  val green: Color,
  val blue: Color,
) {
  companion object {
    @Composable
    fun defaultDarkColors(): PokedexColors = PokedexColors(
      primary = colorResource(id = R.color.colorPrimary),
      background = colorResource(id = R.color.background_dark),
      backgroundLight = colorResource(id = R.color.background800_dark),
      backgroundDark = colorResource(id = R.color.background900_dark),
      absoluteWhite = colorResource(id = R.color.white),
      absoluteBlack = colorResource(id = R.color.black),
      white = colorResource(id = R.color.white_dark),
      white12 = colorResource(id = R.color.white_12_dark),
      white56 = colorResource(id = R.color.white_56_dark),
      white70 = colorResource(id = R.color.white_70_dark),
      black = colorResource(id = R.color.black_dark),
      gray21 = colorResource(id = R.color.gray_21),
      bug = colorResource(id = R.color.bug),
      dark = colorResource(id = R.color.dark),
      dragon = colorResource(id = R.color.dragon),
      electric = colorResource(id = R.color.electric),
      fairy = colorResource(id = R.color.fairy),
      fire = colorResource(id = R.color.fire),
      fighting = colorResource(id = R.color.fighting),
      flying = colorResource(id = R.color.flying),
      ghost = colorResource(id = R.color.ghost),
      steel = colorResource(id = R.color.steel),
      ice = colorResource(id = R.color.ice),
      poison = colorResource(id = R.color.poison),
      psychic = colorResource(id = R.color.psychic),
      rock = colorResource(id = R.color.rock),
      water = colorResource(id = R.color.water),
      grass = colorResource(id = R.color.grass),
      ground = colorResource(id = R.color.ground),
      orange = colorResource(id = R.color.orange),
      green = colorResource(id = R.color.green),
      blue = colorResource(id = R.color.blue),
    )

    @Composable
    fun defaultLightColors(): PokedexColors = PokedexColors(
      primary = colorResource(id = R.color.colorPrimary),
      background = colorResource(id = R.color.background),
      backgroundLight = colorResource(id = R.color.background800),
      backgroundDark = colorResource(id = R.color.background900),
      absoluteWhite = colorResource(id = R.color.white),
      absoluteBlack = colorResource(id = R.color.black),
      white = colorResource(id = R.color.white),
      white12 = colorResource(id = R.color.white_12),
      white56 = colorResource(id = R.color.white_56),
      white70 = colorResource(id = R.color.white_70),
      black = colorResource(id = R.color.black),
      gray21 = colorResource(id = R.color.gray_21),
      bug = colorResource(id = R.color.bug),
      dark = colorResource(id = R.color.dark),
      dragon = colorResource(id = R.color.dragon),
      electric = colorResource(id = R.color.electric),
      fairy = colorResource(id = R.color.fairy),
      fire = colorResource(id = R.color.fire),
      fighting = colorResource(id = R.color.fighting),
      flying = colorResource(id = R.color.flying),
      ghost = colorResource(id = R.color.ghost),
      steel = colorResource(id = R.color.steel),
      ice = colorResource(id = R.color.ice),
      poison = colorResource(id = R.color.poison),
      psychic = colorResource(id = R.color.psychic),
      rock = colorResource(id = R.color.rock),
      water = colorResource(id = R.color.water),
      grass = colorResource(id = R.color.grass),
      ground = colorResource(id = R.color.ground),
      orange = colorResource(id = R.color.orange),
      green = colorResource(id = R.color.green),
      blue = colorResource(id = R.color.blue),
    )
  }
}
