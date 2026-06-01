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
package ashraf.pokedex.mad.core.network.model

import ashraf.pokedex.mad.core.model.Pokemon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network response for GET /pokemon from PokeAPI.
 *
 * Matches the PokeAPI list response JSON shape:
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
