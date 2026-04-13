package ashraf.pokedex.mad.core.data.repository.home

import ashraf.pokedex.mad.core.model.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeHomeRepository : HomeRepository {
  override fun fetchPokemonList(
    page: Int,
    onStart: () -> Unit,
    onComplete: () -> Unit,
    onLastPageReached: () -> Unit,
    onError: (String?) -> Unit,
  ): Flow<List<Pokemon>> = flowOf()

}