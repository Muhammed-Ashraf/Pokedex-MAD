package ashraf.pokedex.mad.feature.home

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ashraf.pokedex.mad.core.data.repository.home.FakeHomeRepository
import ashraf.pokedex.mad.core.designsystem.component.PokedexAppBar
import ashraf.pokedex.mad.core.designsystem.component.PokedexCircularProgress
import ashraf.pokedex.mad.core.designsystem.theme.PokedexTheme
import ashraf.pokedex.mad.core.model.Pokemon
import ashraf.pokedex.mad.core.navigation.PokedexScreen
import ashraf.pokedex.mad.core.navigation.currentComposeNavigator
import ashraf.pokedex.mad.core.preview.PokedexPreviewTheme
import ashraf.pokedex.mad.core.preview.PreviewUtils
import com.kmpalette.palette.graphics.Palette
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.image.LandscapistImage
import com.skydoves.landscapist.palette.PalettePlugin
import com.skydoves.landscapist.palette.rememberPaletteState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/*
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
@OptIn(ExperimentalSharedTransitionApi::class)
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
            navigateToDetails = {
                composeNavigator.navigate(PokedexScreen.Details(pokemon = it))
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
    Box(modifier = Modifier.fillMaxSize()) {
        val threadHold = 8
        LazyVerticalGrid(
            modifier = Modifier.testTag("PokedexList"),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(6.dp)
        ) {
            itemsIndexed(
                items = pokemonList,
                key = { _, pokemon -> pokemon.name },
            ) { index, pokemon ->
                if (index + threadHold > pokemonList.size && uiState != HomeUiState.Loading) {
                    fetchNextPokemonList()
                }
                var palette by rememberPaletteState()
                val backgroundColor by palette.paletteBackgroundColor()
                PokemonCard(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                    onPaletteLoaded = { palette = it },
                    backgroundColor = backgroundColor,
                    pokemon = pokemon,
                    onCardClick = { navigateToDetails(pokemon) }

                )
            }

        }
        if (uiState == HomeUiState.Loading) {
            PokedexCircularProgress()
        }
    }
}

@Composable
private fun PokemonCard(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onPaletteLoaded: (Palette) -> Unit,
    backgroundColor: Color,
    pokemon: Pokemon,
    onCardClick: () -> Unit,
) {
    /**
     * What this does:
     * - Renders a single Pokemon card with image + name.
     * - Applies shared-element transition metadata for smooth Home -> Details animation.
     * - Extracts image palette and reports it back so parent can tint card background.
     *
     * Why this exists:
     * - Keeps card rendering logic isolated and reusable inside grid/list.
     * - Encapsulates image-loading and transition behavior in one place.
     *
     * When to use / how it behaves:
     * - Used by Home grid for each Pokemon item.
     * - Click triggers navigation callback.
     */

    with(sharedTransitionScope) {
        // Why `with(sharedTransitionScope)`:
        // shared transition helper APIs (e.g., sharedBounds, rememberSharedContentState)
        // are extension functions on SharedTransitionScope.
        Card(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth()
                .testTag("Pokemon")
                .sharedBounds(
                    // What this does:
                    // Registers this card as a shared element with a stable key.
                    //
                    // Why key matters:
                    // Home and Details must use the same key for the same item
                    // so Compose can match and animate them between screens.
                    sharedContentState = rememberSharedContentState(key = "pokemon-${pokemon.name}"),

                    // Why animatedVisibilityScope is required:
                    // Provides the transition timing/scope from current nav animation
                    // so shared bounds can animate in sync with destination enter/exit.
                    animatedVisibilityScope = animatedContentScope,
                )
                .clickable { onCardClick() },
            shape = RoundedCornerShape(14.dp),
            // What this does:
            // - Sets card colors for both enabled and disabled states.
            //
            // Why this exists:
            // - Card background is dynamically computed from image palette (`backgroundColor`).
            // - Setting all states explicitly avoids unexpected Material default tint changes.
            //
            // When to use / how it behaves:
            // - `containerColor`: card background in normal state.
            // - `contentColor`: default text/icon color in normal state.
            // - `disabledContainerColor`: card background when disabled.
            // - `disabledContentColor`: default text/icon color when disabled.
            //
            // Trade-off notes:
            // - If inner Text/Icon already sets explicit colors, `contentColor` may have little effect.
            // - Keep separate disabled colors only if you want visually different disabled cards.
            colors = CardColors(
                containerColor = backgroundColor,
                contentColor = backgroundColor,
                disabledContainerColor = backgroundColor,
                disabledContentColor = backgroundColor,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            LandscapistImage(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
                    .size(120.dp),
                imageModel = { pokemon.imageUrl },

                // What this does:
                // Fits full image inside the 120dp box without cropping.
                //
                // Why Inside:
                // Preserves whole artwork visibility for Pokemon images.
                imageOptions = ImageOptions(contentScale = ContentScale.Inside),

                // What this does:
                // - Configures optional image pipeline components for this image request.
                // - Here, it attaches PalettePlugin to extract dominant colors from the loaded image.
                //
                // Why this exists:
                // - Home cards use palette-derived color to tint card background dynamically.
                // - `rememberImageComponent` keeps this component setup stable across recompositions.
                //
                // When to use / how it behaves:
                // - Use this block when image post-processing (palette, animation helpers, etc.) is needed.
                // - Skip plugin setup in Preview (`LocalInspectionMode.current`) to avoid preview instability.
                //
                // Trade-off notes:
                // - Adds extra processing work during image load.
                // - If you don't need palette-based UI, remove this block for simpler/faster rendering.
                component = rememberImageComponent {
                    // Why preview check:
                    // Compose Preview is a design-time environment; some runtime image
                    // plugins can fail or slow previews. Skip palette plugin in preview.
                    if (!LocalInspectionMode.current) { //this is true in compose preview
                        +PalettePlugin(
                            imageModel = pokemon.imageUrl,
                            useCache = true,

                            // What this does:
                            // Sends extracted palette back to parent state so card/list
                            // can derive dynamic background color.
                            paletteLoadedListener = { onPaletteLoaded.invoke(it) },
                        )
                    }
                },
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(12.dp),
                text = pokemon.name,
                color = PokedexTheme.colors.black,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
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

@OptIn(ExperimentalSharedTransitionApi::class)
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