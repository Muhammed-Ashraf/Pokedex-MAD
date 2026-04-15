package ashraf.pokedex.mad.core.data.repository.home

import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import ashraf.pokedex.mad.core.common.network.Dispatcher
import ashraf.pokedex.mad.core.common.network.PokedexAppDispatchers

import ashraf.pokedex.mad.core.database.PokemonDao
import ashraf.pokedex.mad.core.database.entity.mapper.asDomain
import ashraf.pokedex.mad.core.database.entity.mapper.asEntity
import ashraf.pokedex.mad.core.model.Pokemon
import ashraf.pokedex.mad.core.network.model.PokemonResponse
import ashraf.pokedex.mad.core.network.service.PokedexClient
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * Default implementation of HomeRepository.
 *
 * Responsibilities:
 * - Combine network (PokedexClient) and local cache (PokemonDao).
 * - Implement an offline-first strategy:
 *   1) Try DB for this page.
 *   2) If empty, call network and save to DB.
 *   3) Emit the data from DB as the single source of truth.
 */
@VisibleForTesting
class HomeRepositoryImpl @Inject constructor(
    private val pokedexClient: PokedexClient,
    private val pokemonDao: PokemonDao,
    // Inject the IO dispatcher defined in core:common.
    @Dispatcher(PokedexAppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : HomeRepository {

    @WorkerThread
    override fun fetchPokemonList(
        page: Int,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onLastPageReached: () -> Unit,
        onError: (String?) -> Unit,
    ): Flow<List<Pokemon>> = flow {
        // Try to load this page from DB first.
        var pokemons: List<Pokemon> = pokemonDao.getPokemonList(page).asDomain()
        if (pokemons.isEmpty()) {
            // No cache for this page → hit the network via PokedexClient.
            val response: ApiResponse<PokemonResponse> =
                pokedexClient.fetchPokemonList(page = page)
            response
                .suspendOnSuccess {
                    // If next is null, reference treats this as the last page.
                    if (data.next == null) {
                        onLastPageReached()
                    }
                    // Take network results and attach the page index.
                    pokemons = data.results
                    pokemons.forEach { pokemon ->
                        pokemon.page = page
                    }
                    // Save to DB and emit all pages up to this one.
                    pokemonDao.insertPokemonList(pokemons.asEntity())
                    emit(pokemonDao.getAllPokemonList(page).asDomain())
                }
                .onFailure {
                    // Handles all API error cases.
                    onError(message())
                }
        } else {
            // Cache hit: just emit DB data (all pages up to this one).
            emit(pokemonDao.getAllPokemonList(page).asDomain())
        }
    }.onStart { onStart() }
        .onCompletion { onComplete() }
        .flowOn(ioDispatcher)// Run the flow work on the injected IO dispatcher.

}