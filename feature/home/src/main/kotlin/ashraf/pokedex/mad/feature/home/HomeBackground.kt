package ashraf.pokedex.mad.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import ashraf.pokedex.mad.core.designsystem.theme.PokedexTheme
import com.kmpalette.palette.graphics.Palette

/**
 * What this does:
 * - Converts a nullable image [Palette] into a Compose [Color] state.
 * - Returns dominant swatch color when available, otherwise returns theme background color.
 *
 * Why this exists:
 * - Home cards need a dynamic background color that matches each Pokemon image.
 * - Palette extraction can be null/unavailable, so a safe fallback is required.
 *
 * When to use / how it behaves:
 * - Use with image loaders that provide palette results (e.g., PalettePlugin).
 * - Recomputes when the Palette instance changes (`remember(this)`).
 * - Uses `derivedStateOf` so color calculation is efficient during recompositions.
 *
 * Trade-off notes:
 * - Dominant image color may reduce text readability on some images.
 * - If contrast issues appear, clamp or blend the color before applying it to card backgrounds.
 */
@Composable
internal fun Palette?.paletteBackgroundColor(): State<Color> {
    val defaultBackground = PokedexTheme.colors.background
    return remember(this) {
        derivedStateOf {
            val rgb = this?.dominantSwatch?.rgb
            if (rgb != null) {
                Color(rgb)
            } else {
                defaultBackground
            }
        }
    }
}