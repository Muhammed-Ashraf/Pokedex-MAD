package ashraf.pokedex.mad.feature.details

import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import ashraf.pokedex.mad.core.designsystem.R
import ashraf.pokedex.mad.core.designsystem.theme.PokedexTheme
import ashraf.pokedex.mad.core.model.PokemonInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import javax.annotation.concurrent.Immutable

// Marks this class as immutable for Jetpack Compose
// → Compose assumes instances never change after creation
// → Helps optimize recomposition (better performance)
@Immutable
internal data class PokedexStatus(
    // Label like "HP", "ATK", etc.
    val type: String,

    // Ensures value is between 0.0 and 1.0 (used for progress bars)
    @FloatRange(0.0, 1.0)
    val progress: Float,

    // UI color for the stat bar
    val color: Color,
    // Display string (e.g., "45 / 100")
    val label: String,
)

@Composable
internal fun PokemonInfo.toPokedexStatusList(): ImmutableList<PokedexStatus> {

    // persistentListOf creates a TRUE immutable list
    // → Cannot be modified after creation
    // → Any "change" returns a new list instead of mutating
    // → Works well with Compose state & recomposition
    return persistentListOf(

        // HP status
        PokedexStatus(
            type = stringResource(id = R.string.hp),

            // Normalize value to range 0.0 - 1.0 for progress UI
            progress = hp / PokemonInfo.MAX_HP.toFloat(),

            color = PokedexTheme.colors.primary,
            // e.g., "45 / 100"
            label = getHpString(),
        ),

        // Attack status
        PokedexStatus(
            type = stringResource(id = R.string.atk),
            progress = attack / PokemonInfo.MAX_ATTACK.toFloat(),
            color = PokedexTheme.colors.orange,
            label = getAttackString(),
        ),

        // Defense status
        PokedexStatus(
            type = stringResource(id = R.string.def),
            progress = defense / PokemonInfo.MAX_DEFENSE.toFloat(),
            color = PokedexTheme.colors.blue,
            label = getDefenseString(),
        ),

        // Speed status
        PokedexStatus(
            type = stringResource(id = R.string.spd),

            // Same normalization logic applied
            progress = speed / PokemonInfo.MAX_SPEED.toFloat(),

            color = PokedexTheme.colors.flying,
            label = getSpeedString(),
        ),

        // Experience status
        PokedexStatus(
            type = stringResource(id = R.string.exp),
            progress = exp / PokemonInfo.MAX_EXP.toFloat(),
            color = PokedexTheme.colors.green,
            label = getExpString(),
        ),
    )
}