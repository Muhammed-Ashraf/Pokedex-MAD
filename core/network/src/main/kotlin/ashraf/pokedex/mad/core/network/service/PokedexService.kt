package ashraf.pokedex.mad.core.network.service

import ashraf.pokedex.mad.core.network.model.PokemonResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
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
}