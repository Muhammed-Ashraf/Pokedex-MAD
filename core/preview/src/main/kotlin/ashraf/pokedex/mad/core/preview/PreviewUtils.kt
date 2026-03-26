package ashraf.pokedex.mad.core.preview

import ashraf.pokedex.mad.core.model.Pokemon

/**
 * Static fake data for **@Preview** composables.
 *
 * Why a dedicated object (reference: PreviewUtils):
 * - Previews run without network, Room, or Hilt; they need stable sample values.
 * - Central place avoids copying `Pokemon(...)` literals into every preview.
 * - Keeps previews aligned with real [Pokemon] construction (same defaults, same field names).
 */
object PreviewUtils {

    /** Single list row / card preview. */
    fun mockPokemon(): Pokemon = Pokemon(
        page = 0,
        nameField = "bulbasaur",
        url = "https://pokeapi.co/api/v2/pokemon/1/",
    )

    /** Lazy column / grid preview (many rows, cheap to build). */
    fun mockPokemonList(): List<Pokemon> = List(10) { index ->
        Pokemon(page = 0, nameField = "bulbasaur$index", url = "")
    }
//TODO later
    // When you add `PokemonInfo` to core:model (detail screen / API shape), mirror the reference:
    // fun mockPokemonInfo(): PokemonInfo = PokemonInfo(...)
}