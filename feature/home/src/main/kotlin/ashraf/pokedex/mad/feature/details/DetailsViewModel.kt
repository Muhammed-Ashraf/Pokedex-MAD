package ashraf.pokedex.mad.feature.details

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import ashraf.pokedex.mad.core.data.repository.details.DetailsRepository
import ashraf.pokedex.mad.core.model.Pokemon
import ashraf.pokedex.mad.core.model.PokemonInfo
import ashraf.pokedex.mad.core.viewmodel.BaseViewModel
import ashraf.pokedex.mad.core.viewmodel.ViewModelStateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

/**
 * What this does:
 * - ViewModel for the Details screen.
 * - Loads detail data for exactly one [Pokemon] using [DetailsRepository].
 *
 * Why this exists:
 * - Keeps UI state and data loading out of composables.
 * - Uses assisted injection so the ViewModel receives the selected [Pokemon] at creation time.
 *
 * When to use / how it behaves:
 * - Created when user opens Details for a list item.
 * - Hilt provides repository and other app dependencies; navigation provides [Pokemon].
 *
 * Trade-off notes:
 * - Assisted ViewModels need a stable [key] in `hiltViewModel` if the same screen can show
 *   different items; otherwise you may reuse the wrong ViewModel instance.
 */
@HiltViewModel(assistedFactory = DetailsViewModel.Factory::class)
class DetailsViewModel @AssistedInject constructor(
    /**
     * What this does:
     * - Marks [pokemon] as an *assisted* parameter (provided by UI/navigation), not by Hilt graph alone.
     *
     * Why this exists:
     * - Details screen is parameterized by which Pokemon was tapped.
     *
     * When to use / how it behaves:
     * - Passed from `PokedexDetails` via `factory.create(pokemon)`.
     */
    @Assisted private val pokemon: Pokemon,

    /**
     * What this does:
     * - Normal dependency injection from Hilt (singleton/repository binding).
     *
     * Why this exists:
     * - Repository encapsulates network + DB policy for details.
     */
    private val detailsRepository: DetailsRepository,
) : BaseViewModel() {

    /**
     * What this does:
     * - Factory interface Hilt generates an implementation for.
     * - Lets you construct this ViewModel with assisted args while Hilt fills the rest.
     *
     * Why this exists:
     * - `@AssistedInject` constructors cannot be created with `hiltViewModel()` alone;
     *   you need a factory entry point.
     *
     * When to use / how it behaves:
     * - Called from composable `creationCallback` as `factory.create(pokemon)`.
     */
    @AssistedFactory
    interface Factory {
        fun create(pokemon: Pokemon): DetailsViewModel
    }

    internal val uiState: ViewModelStateFlow<DetailsUiState> =
        viewModelStateFlow(DetailsUiState.Loading)

    val pokemonInfo: StateFlow<PokemonInfo?> = flow {
        detailsRepository.fetchPokemonInfo(
            name = pokemon.nameField.replaceFirstChar { it.lowercase() },
            onComplete = {},
            onError = {}).collect { emit(it) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )
}

@Stable
internal sealed interface DetailsUiState {

    data object Idle : DetailsUiState

    data object Loading : DetailsUiState

    data class Error(val message: String?) : DetailsUiState
}