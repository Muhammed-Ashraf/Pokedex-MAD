package ashraf.pokedex.mad.core.data

import app.cash.turbine.test
import ashraf.pokedex.mad.core.data.repository.home.HomeRepositoryImpl
import ashraf.pokedex.mad.core.database.PokemonDao
import ashraf.pokedex.mad.core.database.entity.mapper.asEntity
import ashraf.pokedex.mad.core.network.model.PokemonResponse
import ashraf.pokedex.mad.core.network.service.PokedexClient
import ashraf.pokedex.mad.core.network.service.PokedexService
import ashraf.pokedex.mad.core.test.MainCoroutinesRule
import ashraf.pokedex.mad.core.test.MockUtil.mockPokemonList
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.retrofit.responseOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class HomeRepositoryImplTest {

  // Repository under test
  private lateinit var repository: HomeRepositoryImpl

  // Real client wrapper (built using mocked service)
  private lateinit var client: PokedexClient

  // Mocked network service (no real API calls happen)
  private val service: PokedexService = mock()

  // Mocked database DAO (Room layer is simulated)
  private val pokemonDao: PokemonDao = mock()

  // Rule to control coroutines dispatchers in tests
  @get:Rule
  val coroutinesRule = MainCoroutinesRule()

  @Before
  fun setup() {
    // Wrap mocked service inside client
    client = PokedexClient(service)

    // Create repository with mocked dependencies + test dispatcher
    repository = HomeRepositoryImpl(
      client,
      pokemonDao,
      coroutinesRule.testDispatcher
    )
  }

  /**
   * CASE 1:
   * When database is empty → repository should fetch from network
   */
  @Test
  fun fetchPokemonListFromNetworkTest() = runTest {

    // Fake API response data
    val mockData =
        PokemonResponse(
            count = 984,
            next = null,
            previous = null,
            results = mockPokemonList()
        )

    // DB returns empty list → forces network call
    whenever(pokemonDao.getPokemonList(page_ = 0))
      .thenReturn(emptyList())

    // DB after insert returns mapped entity list
    whenever(pokemonDao.getAllPokemonList(page_ = 0))
      .thenReturn(mockData.results.asEntity())

    // Mock API response
    whenever(service.fetchPokemonList())
      .thenReturn(
        ApiResponse.responseOf {
          Response.success(mockData)
        }
      )

    // Call repository function and test emitted Flow
    repository.fetchPokemonList(
      page = 0,
      onStart = {},
      onComplete = {},
      onLastPageReached = {},
      onError = {}
    ).test(2.toDuration(DurationUnit.SECONDS)) {

      // First emitted item from Flow
      val actualItem = awaitItem()[0]

      // Validate mapped values
      assertEquals(0, actualItem.page)
      assertEquals("Bulbasaur", actualItem.name)
      assertEquals("https://pokeapi.co/api/v2/pokemon/1/", actualItem.url)

      // Ensure Flow completes
      awaitComplete()
    }

    // Verify DB was checked
    verify(pokemonDao, atLeastOnce()).getPokemonList(page_ = 0)

    // Verify API was called
    verify(service, atLeastOnce()).fetchPokemonList()

    // Verify data was saved into DB
    verify(pokemonDao, atLeastOnce())
      .insertPokemonList(mockData.results.asEntity())

    // Ensure no extra API calls happened
    verifyNoMoreInteractions(service)
  }

  /**
   * CASE 2:
   * When database already has data → no network call should happen
   */
  @Test
  fun fetchPokemonListFromDatabaseTest() = runTest {

    // Fake data
    val mockData =
      PokemonResponse(
        count = 984,
        next = null,
        previous = null,
        results = mockPokemonList()
      )

    // DB already has data → should be used directly
    whenever(pokemonDao.getPokemonList(page_ = 0))
      .thenReturn(mockData.results.asEntity())

    // Full DB fetch returns same data
    whenever(pokemonDao.getAllPokemonList(page_ = 0))
      .thenReturn(mockData.results.asEntity())

      // Mock API response
      whenever(service.fetchPokemonList())
          .thenReturn(
              ApiResponse.responseOf {
                  Response.success(mockData)
              }
          )

    // Call repository
    repository.fetchPokemonList(
      page = 0,
      onStart = {},
      onComplete = {},
      onLastPageReached = {},
      onError = {},
    ).test(2.toDuration(DurationUnit.SECONDS)) {

      // First emitted item
      val actualItem = awaitItem()[0]

      // Validate values
      assertEquals(0, actualItem.page)
      assertEquals("Bulbasaur", actualItem.name)
      assertEquals("https://pokeapi.co/api/v2/pokemon/1/", actualItem.url)

      // Flow should finish
      awaitComplete()
    }

    // Verify DB was accessed
    verify(pokemonDao, atLeastOnce()).getPokemonList(page_ = 0)

    // Verify full DB read happened
    verify(pokemonDao, atLeastOnce()).getAllPokemonList(page_ = 0)

    // IMPORTANT: No service verification → network must NOT be called
      verify(service, never()).fetchPokemonList()
  }
}