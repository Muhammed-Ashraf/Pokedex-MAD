package ashraf.pokedex.mad.feature.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ashraf.pokedex.mad.core.data.repository.home.HomeRepository
import ashraf.pokedex.mad.core.model.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

/**
 * What this does:
 * - Manages the Home screen state and paginated Pokemon loading.
 * - Exposes two observable streams:
 *   1) `uiState` for screen status (Loading / Idle / Error)
 *   2) `pokemonList` for the loaded items to render.
 *
 * Why this exists:
 * - Keeps business/data loading logic out of Composables.
 * - Coordinates repository callbacks with UI-friendly state updates.
 * - Provides a single source of truth for Home screen behavior.
 *
 * When to use / how it behaves:
 * - Constructed by Hilt when Home screen asks for `hiltViewModel()`.
 * - Starts from page 0 and fetches items automatically.
 * - Each call to `fetchNextPokemonList()` increments page index and triggers another fetch,
 *   unless loading is in progress or the last page was reached.
 *
 * Trade-off notes:
 * - `flatMapLatest` cancels older in-flight page fetches if a new page index is emitted quickly.
 *   This keeps state fresh, but may drop intermediate requests in very aggressive scrolling cases.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
) : ViewModel() {

    /**
     * What this does:
     * - Holds lightweight screen status.
     *
     * Why this exists:
     * - UI needs explicit status to show loading indicators and error states.
     *
     * When to use / how it behaves:
     * - Updated from repository callbacks: start, complete, and error.
     */
    internal val uiState: StateFlow<HomeUiState>
        field = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    /**
     * What this does:
     * - Tracks whether pagination reached the end.
     *
     * Why this exists:
     * - Prevents unnecessary extra network/database requests.
     *
     * When to use / how it behaves:
     * - Set to true when repository reports "last page reached".
     */
    private var isLastPageReached = false

    /**
     * What this does:
     * - Acts as a paging trigger (current page index).
     *
     * Why this exists:
     * - Turning page number into a Flow allows declarative fetching with operators.
     *
     * When to use / how it behaves:
     * - Starts at 0, so first collection fetches page 0.
     * - Incremented by `fetchNextPokemonList()`.
     */
    private val pokemonFetchingIndex: MutableStateFlow<Int> = MutableStateFlow(0)

    /**
     * What this does:
     * - Emits the latest list of Pokemon for the UI to render.
     *
     * Why this exists:
     * - Converts repository paging flow into a lifecycle-aware `StateFlow`.
     *
     * When to use / how it behaves:
     * - Re-fetches when `pokemonFetchingIndex` changes.
     * - Uses `stateIn` so Compose can collect stable current state.
     * - Keeps upstream active for 5 seconds after last subscriber unsubscribes.
     *
     * Trade-off notes:
     * - `SharingStarted.WhileSubscribed(5_000)` reduces restart churn during quick
     *   lifecycle changes, but keeps upstream active briefly (small extra work).
     */
    val pokemonList: StateFlow<List<Pokemon>> =
        pokemonFetchingIndex
            .flatMapLatest { page ->
                homeRepository.fetchPokemonList(
                    page = page,
                    onStart = { uiState.tryEmit( HomeUiState.Loading) },
                    onComplete = { uiState.tryEmit( HomeUiState.Idle) },
                    onLastPageReached = { isLastPageReached = true },
                    onError = { message -> uiState.tryEmit( HomeUiState.Error(message)) },
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    /**
     * What this does:
     * - Requests loading the next page.
     *
     * Why this exists:
     * - Called by UI pagination triggers (e.g., near end of list/grid).
     *
     * When to use / how it behaves:
     * - No-op if currently loading or if all pages are already loaded.
     */
    fun fetchNextPokemonList() {
        if (uiState.value != HomeUiState.Loading && !isLastPageReached) {
            pokemonFetchingIndex.value++
        }
    }
}

/**
 * What this does:
 * - Represents Home screen status for rendering decisions.
 *
 * Why this exists:
 * - Keeps status modeling explicit and type-safe.
 *
 * When to use / how it behaves:
 * - `Loading`: show progress UI.
 * - `Idle`: normal content state.
 * - `Error`: show error feedback.
 *
 * Trade-off notes:
 * - This models status only; actual list data is exposed separately as `pokemonList`.
 */
@Stable
internal sealed interface HomeUiState {
    data object Idle : HomeUiState
    data object Loading : HomeUiState
    data class Error(val message: String?) : HomeUiState
}