package ashraf.pokedex.mad.core.network.model

import ashraf.pokedex.mad.core.model.Pokemon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network response for GET /pokemon from PokeAPI.
 *
 * This mirrors the JSON shape:
 * {
 *   "count": 1302,
 *   "next": "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
 *   "previous": null,
 *   "results": [ { "name": "...", "url": "..." }, ... ]
 * }
 */

@Serializable
data class PokemonResponse(
    @SerialName(value = "count") val count: Int,
    @SerialName(value = "next") val next: String?,
    @SerialName(value = "previous") val previous: String?,
    @SerialName(value = "results") val results: List<Pokemon>,
)
