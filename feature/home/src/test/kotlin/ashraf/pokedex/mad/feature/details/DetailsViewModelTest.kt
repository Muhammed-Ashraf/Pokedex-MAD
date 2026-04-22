package ashraf.pokedex.mad.feature.details

import ashraf.pokedex.mad.core.data.repository.details.DetailsRepository
import ashraf.pokedex.mad.core.model.Pokemon
import ashraf.pokedex.mad.core.model.PokemonInfo
import ashraf.pokedex.mad.core.test.MainCoroutinesRule
import ashraf.pokedex.mad.core.test.MockUtil.mockPokemonInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class DetailsViewModelTest {

  @get:Rule
  val coroutinesRule = MainCoroutinesRule()

  @Test
  fun fetchPokemonInfo_uses_nameField_with_first_character_lowercased() =
    coroutinesRule.testScope.runTest {
      val pokemon =
        Pokemon(
          page = 0,
          nameField = "Charmander",
          url = "https://pokeapi.co/api/v2/pokemon/4/",
        )
      val stub =
        StubDetailsRepository { name ->
          assertEquals("charmander", name)
          flowOf(mockPokemonInfo().copy(name = "charmander"))
        }
      val viewModel = DetailsViewModel(pokemon = pokemon, detailsRepository = stub)

      backgroundScope.launch { viewModel.pokemonInfo.collect {} }

      assertEquals("charmander", viewModel.pokemonInfo.value?.name)
      assertEquals(listOf("charmander"), stub.requestedNames)
    }

  @Test
  fun pokemonInfo_reflects_repository_emission() =
    coroutinesRule.testScope.runTest {
      val pokemon = mockPokemon()
      val info = mockPokemonInfo()
      val stub =
        StubDetailsRepository { _ ->
          flowOf(info)
        }
      val viewModel = DetailsViewModel(pokemon = pokemon, detailsRepository = stub)

      backgroundScope.launch { viewModel.pokemonInfo.collect {} }

      assertEquals(info, viewModel.pokemonInfo.value)
    }

  @Test
  fun pokemonInfo_stays_null_when_repository_emits_nothing() =
    coroutinesRule.testScope.runTest {
      val pokemon = mockPokemon()
      val stub =
        StubDetailsRepository { _ ->
          emptyFlow()
        }
      val viewModel = DetailsViewModel(pokemon = pokemon, detailsRepository = stub)

      backgroundScope.launch { viewModel.pokemonInfo.collect {} }

      assertNull(viewModel.pokemonInfo.value)
      assertEquals(listOf("bulbasaur"), stub.requestedNames)
    }

  @Test
  fun pokemonInfo_stays_null_when_repository_invokes_onError_without_emit() =
    coroutinesRule.testScope.runTest {
      val pokemon = mockPokemon()
      val repository =
        object : DetailsRepository {
          override fun fetchPokemonInfo(
            name: String,
            onFetchComplete: () -> Unit,
            onFetchError: (String?) -> Unit,
          ): Flow<PokemonInfo> =
            flow<PokemonInfo> {
              assertEquals("bulbasaur", name)
              onFetchError("network")
            }
              .onCompletion { onFetchComplete() }
        }
      val viewModel = DetailsViewModel(pokemon = pokemon, detailsRepository = repository)

      backgroundScope.launch { viewModel.pokemonInfo.collect {} }

      assertNull(viewModel.pokemonInfo.value)
    }

  private fun mockPokemon(): Pokemon =
    Pokemon(
      page = 0,
      nameField = "bulbasaur",
      url = "https://pokeapi.co/api/v2/pokemon/1/",
    )

  private class StubDetailsRepository(
    private val flowForName: (String) -> Flow<PokemonInfo>,
  ) : DetailsRepository {
    val requestedNames = mutableListOf<String>()

    override fun fetchPokemonInfo(
      name: String,
      onFetchComplete: () -> Unit,
      onFetchError: (String?) -> Unit,
    ): Flow<PokemonInfo> =
      flow {
        requestedNames.add(name)
        flowForName(name).collect { emit(it) }
      }
        .onCompletion { onFetchComplete() }
  }
}
