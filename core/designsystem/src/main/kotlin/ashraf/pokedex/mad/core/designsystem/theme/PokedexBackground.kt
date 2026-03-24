package ashraf.pokedex.mad.core.designsystem.theme

/**
 * Screen / scaffold background token (color + optional tonal elevation).
 *
 * **Why not rely only on Material `surface`?** The reference sets an explicit full-screen
 * background on a [androidx.compose.foundation.layout.Box] in [PokedexTheme], so the root
 * always matches the app palette from XML. Material’s default template instead relies on
 * [androidx.compose.material3.MaterialTheme] colorScheme for surfaces.
 *
 * Provided through [LocalBackgroundTheme] and read via `PokedexTheme.background`.
 */

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ashraf.pokedex.mad.core.designsystem.R

@Immutable
data class PokedexBackground(
    val color: Color = Color.Unspecified,
    val tonalElevation: Dp = Dp.Unspecified,
) {
    companion object {
        @Composable
        fun defaultBackground(darkTheme: Boolean): PokedexBackground {
            return if (darkTheme) {
                PokedexBackground(
                    color = colorResource(id = R.color.background_dark),
                    tonalElevation = 0.dp,
                )
            } else {
                PokedexBackground(
                    color = colorResource(id = R.color.background),
                    tonalElevation = 0.dp,
                )
            }
        }
    }
}

val LocalBackgroundTheme: ProvidableCompositionLocal<PokedexBackground> =
    staticCompositionLocalOf { PokedexBackground() }