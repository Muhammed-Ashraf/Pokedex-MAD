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
package ashraf.pokedex.mad.core.network.service

import ashraf.pokedex.mad.core.model.PokemonInfo
import ashraf.pokedex.mad.core.network.model.PokemonResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API for PokeAPI v2.
 * Base URL is set in NetworkModule as https://pokeapi.co/api/v2/
 * so these paths are relative to that (e.g. "pokemon" -> .../v2/pokemon).
 *
 * Returns [ApiResponse] (Sandwich) so callers get Success(data) or Failure without try/catch.
 * Use .suspendOnSuccess { data -> } and .onFailure { } in repository/ViewModel.
 */
interface PokedexService {

  @GET("pokemon")
  suspend fun fetchPokemonList(
    @Query("limit") limit: Int = 20,
    @Query("offset") offset: Int = 0,
  ): ApiResponse<PokemonResponse>

  @GET("pokemon/{name}")
  suspend fun fetchPokemonInfo(@Path("name") name: String): ApiResponse<PokemonInfo>
}
