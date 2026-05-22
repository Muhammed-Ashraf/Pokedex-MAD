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

import ashraf.pokedex.mad.core.model.Pokemon
import ashraf.pokedex.mad.core.model.PokemonInfo

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

    fun mockPokemonList(): List<Pokemon> {
        val pokemons = listOf(
            "bulbasaur" to 1,
            "charmander" to 4,
            "squirtle" to 7,
            "pikachu" to 25,
            "eevee" to 133,
            "snorlax" to 143,
            "mewtwo" to 150,
            "gengar" to 94,
        )
        return pokemons.map { (name, id) ->
            Pokemon(page = 0, nameField = name, url = "https://pokeapi.co/api/v2/pokemon/$id/")
        }
    }

    fun mockPokemonInfo() = PokemonInfo(
        id = 1,
        name = "bulbasaur",
        height = 7,
        weight = 69,
        experience = 60,
        types = listOf(
            PokemonInfo.TypeResponse(slot = 0, type = PokemonInfo.Type("grass")),
            PokemonInfo.TypeResponse(slot = 0, type = PokemonInfo.Type("poison")),
        ),
        stats = listOf(
            PokemonInfo.StatsResponse(baseStat = 20, effort = 0, stat = PokemonInfo.Stat("hp")),
            PokemonInfo.StatsResponse(baseStat = 40, effort = 0, stat = PokemonInfo.Stat("attack")),
            PokemonInfo.StatsResponse(
                baseStat = 60,
                effort = 0,
                stat = PokemonInfo.Stat("defense"),
            ),
            PokemonInfo.StatsResponse(baseStat = 80, effort = 0, stat = PokemonInfo.Stat("attack")),
        ),
    )
}
