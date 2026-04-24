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
package ashraf.pokedex.mad.core.data.repository.home

import androidx.annotation.WorkerThread
import ashraf.pokedex.mad.core.model.Pokemon
import kotlinx.coroutines.flow.Flow

/**
 * Data-layer contract for the Home screen.
 *
 * The UI / ViewModel depends on this interface instead of knowing about
 * Retrofit, Room, or any low-level data source details.
 */
interface HomeRepository {

  /**
   * Fetches a page of Pokemon as a cold Flow.
   *
   * - `page`           : logical page index (0, 1, 2, …) used by the repository
   *                      to calculate offset/limit and to talk to DB/Network.
   * - `onStart`        : called when the flow collection starts
   *                      (e.g. show loading indicator).
   * - `onComplete`     : called when the flow completes
   *                      (e.g. hide loading indicator).
   * - `onLastPageReached` : called when the repository decides there are
   *                         no more pages from the server.
   * - `onError`        : called with an error message when something goes wrong
   *                      (network error, parsing, etc.).
   *
   * The Flow emits a list of domain Pokemon models that the UI can render.
   */
  @WorkerThread
  fun fetchPokemonList(
    page: Int,
    onStart: () -> Unit = {},
    onComplete: () -> Unit = {},
    onLastPageReached: () -> Unit = {},
    onError: (String?) -> Unit = {},
  ): Flow<List<Pokemon>>
}
