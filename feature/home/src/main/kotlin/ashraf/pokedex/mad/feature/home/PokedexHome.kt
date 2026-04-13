import android.content.res.Configuration
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ashraf.pokedex.mad.core.data.repository.home.FakeHomeRepository
import ashraf.pokedex.mad.core.designsystem.component.PokedexAppBar
import ashraf.pokedex.mad.core.model.Pokemon
import ashraf.pokedex.mad.core.navigation.currentComposeNavigator
import ashraf.pokedex.mad.core.preview.PokedexPreviewTheme
import ashraf.pokedex.mad.core.preview.PreviewUtils
import ashraf.pokedex.mad.feature.home.HomeUiState
import ashraf.pokedex.mad.feature.home.HomeViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * What this does:
 * - Entry composable for Home feature.
 * - Collects ViewModel state and forwards data/actions to UI content.
 *
 * Why this exists:
 * - Keeps state collection and navigation wiring at the route boundary.
 * - Keeps `HomeContent` easy to preview/test as a pure UI function.
 *
 * When to use / how it behaves:
 * - Called from app navigation graph for Home destination.
 */
@OptIn(ExperimentalSharedTransitionApi::class) //todo check whether this can be removed after adding in NAV host
@Composable
fun PokedexHome(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val pokemonList by homeViewModel.pokemonList.collectAsStateWithLifecycle()

    val composeNavigator = currentComposeNavigator

    Column(modifier = Modifier.fillMaxSize()) {
        PokedexAppBar {
            //todo later
        }
        HomeContent(
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            uiState = uiState,
            pokemonList = pokemonList.toImmutableList(),
            fetchNextPokemonList = homeViewModel::fetchNextPokemonList,
            navigateToDetails = { //todo
            }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun HomeContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    uiState: HomeUiState,
    pokemonList: ImmutableList<Pokemon>,
    fetchNextPokemonList: () -> Unit,
    navigateToDetails: (Pokemon) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize())
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PokedexHomePreview() {
    PokedexPreviewTheme { animatedContentScope ->
        PokedexHome(
            sharedTransitionScope = this@PokedexPreviewTheme,
            animatedContentScope = animatedContentScope,
            homeViewModel = HomeViewModel(homeRepository = FakeHomeRepository())
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class) //todo check last whether this is needed
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeContentPreview() {
    PokedexPreviewTheme { animatedContentScope ->
        HomeContent(
            sharedTransitionScope = this@PokedexPreviewTheme,
            animatedContentScope = animatedContentScope,
            uiState = HomeUiState.Idle,
            pokemonList = PreviewUtils.mockPokemonList().toImmutableList(),
            fetchNextPokemonList = {},
            navigateToDetails = {}
        )

    }
}