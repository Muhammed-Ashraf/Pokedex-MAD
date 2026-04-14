/*
 * Designed and developed by 2024 skydoves (Jaewoong Eum)
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

package ashraf.pokedex.mad.core.network.service

import ashraf.pokedex.mad.core.model.PokemonInfo
import ashraf.pokedex.mad.core.network.model.PokemonResponse
import com.skydoves.sandwich.ApiResponse
import javax.inject.Inject

/**
 * Thin network client that wraps [PokedexService].
 *
 * Purpose (aligned with reference project):
 * - Centralizes how the app talks to the PokeAPI service.
 * - Encapsulates paging parameters (limit / offset) so callers only care about "page".
 * - Returns Sandwich's [ApiResponse] so higher layers (repositories) can use
 *   the same success/error handling pattern everywhere.
 *
 * This class lives in the network layer and will be injected into repositories
 * in core:data (e.g. HomeRepositoryImpl) instead of injecting [PokedexService] directly.
 */
class PokedexClient @Inject constructor(
    // Retrofit interface that actually defines the HTTP calls.
    private val pokedexService: PokedexService,
) {

    /**
     * Fetches one "page" of Pokemon from the API.
     *
     * Inputs:
     * - [page]: a zero-based page index (0, 1, 2, ...).
     *
     * How it works:
     * - Converts page into (limit, offset) pair understood by the PokeAPI:
     *     limit  = PAGING_SIZE (fixed page size)
     *     offset = page * PAGING_SIZE
     * - Delegates the actual HTTP call to [pokedexService].
     * - Returns an [ApiResponse] so the caller can use Sandwich operators
     *   (suspendOnSuccess / suspendOnError) for consistent error handling.
     */
    suspend fun fetchPokemonList(page: Int): ApiResponse<PokemonResponse> =
        pokedexService.fetchPokemonList(
            limit = PAGING_SIZE,
            offset = page * PAGING_SIZE,
        )

    suspend fun fetchPokemonInfo(name: String): ApiResponse<PokemonInfo> =
        pokedexService.fetchPokemonInfo(name)

    companion object {
        // Number of items per "page" when requesting the Pokemon list.
        // Kept here so both client and repositories share the same paging size.
        private const val PAGING_SIZE = 20
    }
}
