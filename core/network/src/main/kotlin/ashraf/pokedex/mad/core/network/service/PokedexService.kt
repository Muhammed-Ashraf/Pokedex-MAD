package ashraf.pokedex.mad.core.network.service

import ashraf.pokedex.mad.core.network.model.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API for PokeAPI v2.
 * Base URL is set in NetworkModule as https://pokeapi.co/api/v2/
 * so these paths are relative to that (e.g. "pokemon" -> .../v2/pokemon).
 */
interface PokedexService {

    @GET("pokemon")
    suspend fun fetchPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
    ): PokemonResponse
}