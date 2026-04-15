package ashraf.pokedex.mad.core.data.repository.details

import ashraf.pokedex.mad.core.model.PokemonInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeDetailsRepository : DetailsRepository {
    override fun fetchPokemonInfo(
        name: String,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    ): Flow<PokemonInfo> = flowOf()
}