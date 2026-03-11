//package ashraf.pokedex.mad.core.data.repository.home
//
//import ashraf.pokedex.mad.core.database.PokemonDao
//import ashraf.pokedex.mad.core.database.entity.mapper.asDomain
//import ashraf.pokedex.mad.core.database.entity.mapper.asEntity
//import ashraf.pokedex.mad.core.model.Pokemon
//import ashraf.pokedex.mad.core.network.model.PokemonResponse
//import ashraf.pokedex.mad.core.network.service.PokedexService
//import com.skydoves.sandwich.ApiResponse
//import com.skydoves.sandwich.message
//import com.skydoves.sandwich.suspendOnError
//import com.skydoves.sandwich.suspendOnSuccess
//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.flowOn
//import kotlinx.coroutines.flow.onCompletion
//import kotlinx.coroutines.flow.onStart
//
///**
// * Default data-layer implementation for HomeRepository.
// *
// * Combines:
// * - Network (PokedexService)
// * - Local cache (PokemonDao)
// * - Mapping between domain models and Room entities (asEntity / asDomain).
// */
//class HomeRepositoryImpl(
//    private val service: PokedexService,
//    private val pokemonDao: PokemonDao,
//    @Dispatcher(PokedexAppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
//) : HomeRepository {
//
//    /**
//     * Offline-first strategy:
//     * 1) Try DB for this page.
//     * 2) If empty, call network and save to DB.
//     * 3) Emit the final list for this page from DB.
//     */
//    override fun fetchPokemonList(
//        page: Int,
//        onStart: () -> Unit,
//        onComplete: () -> Unit,
//        onLastPageReached: () -> Unit,
//        onError: (String) -> Unit
//    ): Flow<List<Pokemon>> = flow {
//        onStart()
//
//        // 1) Try to get data from local cache first.
//        val localEntities = pokemonDao.getPokemonList(page)
//        val localDomain = localEntities.asDomain()
//
//        if (localDomain.isNotEmpty()) {
//            // We already have this page cached → just emit it.
//            emit(localDomain)
//            return@flow
//        }
//
//        // 2) Cache is empty for this page → hit the network.
//        //    Page → offset/limit mapping can match the reference (e.g. 20 per page).
//        val limit = 20
//        val offset = page * limit
//
//        val response: ApiResponse<PokemonResponse> =
//            service.fetchPokemonList(limit = limit, offset = offset)
//
//        response
//            .suspendOnSuccess {
//                val results = data.results
//
//                // If no results, we reached the last page.
//                if (results.isEmpty()) {
//                    onLastPageReached()
//                    emit(emptyList())
//                    return@suspendOnSuccess
//                }
//
//                // 2a) Save network data into DB as entities (with page info).
//                val entities = results.asEntity()
//                pokemonDao.insertPokemonList(entities)
//
//                // 3) Read back from DB to keep a single source of truth.
//                val updated = pokemonDao.getPokemonPage(page).asDomain().orEmpty()
//                emit(updated)
//            }
//            .suspendOnError {
//                // Map Sandwich error to a simple message for now.
//                onError(message())
//            }
//    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher) // Run the work on IO dispatcher.
//}
//
