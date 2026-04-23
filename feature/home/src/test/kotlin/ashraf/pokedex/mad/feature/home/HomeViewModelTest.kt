package ashraf.pokedex.mad.feature.home

import ashraf.pokedex.mad.core.data.repository.home.HomeRepository
import ashraf.pokedex.mad.core.model.Pokemon
import ashraf.pokedex.mad.core.test.MainCoroutinesRule
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

  @get:Rule
  val coroutinesRule = MainCoroutinesRule()

  @Test
  fun initial_load_requests_page_zero_and_exposes_pokemon_list() =
    coroutinesRule.testScope.runTest {
      val stub = StubHomeRepository()
      stub.givenPage(
        page = 0,
        items = listOf(pokemon("bulbasaur", 1)),
        isLastPage = false,
      )
      val viewModel = HomeViewModel(stub)

      backgroundScope.launch { viewModel.pokemonList.collect {} }

      assertEquals(listOf(pokemon("bulbasaur", 1)), viewModel.pokemonList.value)
      assertEquals(listOf(0), stub.requestedPages)
      assertEquals(HomeUiState.Idle, viewModel.uiState.value)
    }

  @Test
  fun fetch_next_increments_page_and_loads_next_batch() =
    coroutinesRule.testScope.runTest {
      val stub = StubHomeRepository()
      stub.givenPage(0, listOf(pokemon("bulbasaur", 1)))
      stub.givenPage(
        1,
        listOf(
          pokemon("bulbasaur", 1),
          pokemon("ivysaur", 2),
        ),
      )
      val viewModel = HomeViewModel(stub)
      backgroundScope.launch { viewModel.pokemonList.collect {} }

      assertEquals(HomeUiState.Idle, viewModel.uiState.value)

      viewModel.fetchNextPokemonList()

      assertEquals(
        listOf(
          pokemon("bulbasaur", 1),
          pokemon("ivysaur", 2),
        ),
        viewModel.pokemonList.value,
      )
      assertEquals(listOf(0, 1), stub.requestedPages)
    }

  @Test
  fun fetch_next_is_ignored_while_loading() =
    coroutinesRule.testScope.runTest {
      val stub = HangingHomeRepository()
      val viewModel = HomeViewModel(stub)
      backgroundScope.launch { viewModel.pokemonList.collect {} }

      assertEquals(HomeUiState.Loading, viewModel.uiState.value)
      assertEquals(listOf(0), stub.requestedPages)

      viewModel.fetchNextPokemonList()
      viewModel.fetchNextPokemonList()

      assertEquals(listOf(0), stub.requestedPages)
    }

  @Test
  fun fetch_next_is_ignored_after_last_page() =
    coroutinesRule.testScope.runTest {
      val stub = StubHomeRepository()
      stub.givenPage(
        page = 0,
        items = listOf(pokemon("bulbasaur", 1)),
        isLastPage = true,
      )
      val viewModel = HomeViewModel(stub)
      backgroundScope.launch { viewModel.pokemonList.collect {} }

      assertEquals(HomeUiState.Idle, viewModel.uiState.value)

      viewModel.fetchNextPokemonList()

      assertEquals(listOf(0), stub.requestedPages)
    }

  private fun pokemon(name: String, id: Int): Pokemon =
    Pokemon(
      page = 0,
      nameField = name,
      url = "https://pokeapi.co/api/v2/pokemon/$id/",
    )

  private class StubHomeRepository : HomeRepository {
    private val pages = mutableMapOf<Int, PageSpec>()
    val requestedPages = mutableListOf<Int>()

    fun givenPage(
      page: Int,
      items: List<Pokemon>,
      isLastPage: Boolean = false,
      errorMessage: String? = null,
    ) {
      pages[page] = PageSpec(items, isLastPage, errorMessage)
    }

    override fun fetchPokemonList(
      page: Int,
      onFetchStart: () -> Unit,
      onFetchComplete: () -> Unit,
      onLastPageReached: () -> Unit,
      onError: (String?) -> Unit,
    ): Flow<List<Pokemon>> =
      flow {
        requestedPages.add(page)
        val spec = pages[page]
        if (spec == null) {
          emit(emptyList())
          return@flow
        }
        if (spec.errorMessage != null) {
          onError(spec.errorMessage)
          return@flow
        }
        emit(spec.items)
        if (spec.isLastPage) {
          onLastPageReached()
        }
      }
        .onStart { onFetchStart() }
        .onCompletion { onFetchComplete() }
  }

  private class HangingHomeRepository : HomeRepository {
    val requestedPages = mutableListOf<Int>()

    override fun fetchPokemonList(
      page: Int,
      onFetchStart: () -> Unit,
      onFetchComplete: () -> Unit,
      onLastPageReached: () -> Unit,
      onError: (String?) -> Unit,
    ): Flow<List<Pokemon>> =
      flow<List<Pokemon>> {
        requestedPages.add(page)
        awaitCancellation()
      }
        .onStart { onFetchStart() }
        .onCompletion { onFetchComplete() }
  }

  private data class PageSpec(
    val items: List<Pokemon>,
    val isLastPage: Boolean,
    val errorMessage: String?,
  )
}
