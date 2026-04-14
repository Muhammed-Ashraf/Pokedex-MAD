package ashraf.pokedex.mad.core.data.repository.details

import androidx.annotation.WorkerThread
import ashraf.pokedex.mad.core.model.PokemonInfo
import kotlinx.coroutines.flow.Flow

interface DetailsRepository {

    @WorkerThread
    fun fetchPokemonInfo(
        name: String,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    ): Flow<PokemonInfo>
}