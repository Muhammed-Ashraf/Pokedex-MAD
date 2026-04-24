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
package ashraf.pokedex.mad.core.data.repository.details

import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import ashraf.pokedex.mad.core.common.network.Dispatcher
import ashraf.pokedex.mad.core.common.network.PokedexAppDispatchers
import ashraf.pokedex.mad.core.database.PokemonInfoDao
import ashraf.pokedex.mad.core.database.entity.mapper.asDomain
import ashraf.pokedex.mad.core.database.entity.mapper.asEntity
import ashraf.pokedex.mad.core.model.PokemonInfo
import ashraf.pokedex.mad.core.network.model.PokemonErrorResponse
import ashraf.pokedex.mad.core.network.model.mapper.ErrorResponseMapper
import ashraf.pokedex.mad.core.network.service.PokedexClient
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

@VisibleForTesting
class DetailsRepositoryImpl @Inject constructor(
  private val pokedexClient: PokedexClient,
  private val pokemonInfoDao: PokemonInfoDao,
  @Dispatcher(PokedexAppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) :
  DetailsRepository {

  @WorkerThread
  override fun fetchPokemonInfo(
    name: String,
    onComplete: () -> Unit,
    onError: (String?) -> Unit,
  ): Flow<PokemonInfo> = flow {
    val pokemonInfo = pokemonInfoDao.getPokemonInfo(name)
    if (pokemonInfo == null) {
      val response = pokedexClient.fetchPokemonInfo(name)
      response.suspendOnSuccess {
        pokemonInfoDao.insertPokemonInfo(data.asEntity())
        emit(data)
      }
        .onError {
          /** maps the [ApiResponse.Failure.Error] to the [PokemonErrorResponse] using the mapper. */
          map(ErrorResponseMapper) { onError("[Code: $code]: $message") }
        }
        .onException { onError(message) }
    } else {
      emit(pokemonInfo.asDomain())
    }
  }.onCompletion { onComplete() }.flowOn(ioDispatcher)
}
