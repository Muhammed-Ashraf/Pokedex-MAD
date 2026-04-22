package ashraf.pokedex.mad.core.network

import ashraf.pokedex.mad.core.network.service.PokedexService
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
// This test class extends ApiAbstract → gives us:
// - MockWebServer (fake backend)
// - enqueueResponse()
// - createService()
class PokedexServiceTest : ApiAbstract<PokedexService>() {

  // Retrofit API interface
  private lateinit var service: PokedexService

  @Before
  fun initService() {
    // Creates Retrofit service using MockWebServer as base URL
    // So ALL API calls go to fake server, not real internet
    service = createService(PokedexService::class.java)
  }

  @Throws(IOException::class)
  @Test
  fun fetchPokemonListFromNetworkTest() = runTest {

    // Loads JSON file from:
    // test/resources/api-response/PokemonResponse.json
    // and enqueues it into MockWebServer
    // → Next API call will return this JSON
    enqueueResponse("PokemonResponse.json")

    // Call API method
    // This DOES NOT hit real network
    // It hits MockWebServer and gets the JSON we enqueued above
    val response = service.fetchPokemonList()

    // Cast ApiResponse → Success and extract parsed data
    // requireNotNull ensures test fails if data is null
    val responseBody = requireNotNull((response as ApiResponse.Success).data)

    // Verify JSON was parsed correctly into Kotlin model

    // From JSON: "count": 964
    assertThat(responseBody.count, `is`(964))

    // Raw API name is on nameField; `name` is a UI getter that capitalizes (see Pokemon in core:model).
    assertThat(responseBody.results[0].nameField, `is`("bulbasaur"))
    assertThat(
      responseBody.results[0].url,
      `is`("https://pokeapi.co/api/v2/pokemon/1/")
    )
  }

  @Throws(IOException::class)
  @Test
  fun fetchPokemonInfoFromNetworkTest() = runTest {

    // Enqueue fake response for a single Pokémon
    enqueueResponse("Bulbasaur.json")

    // Call API with parameter "bulbasaur"
    // NOTE: MockWebServer does NOT care about this parameter by default
    // It just returns the queued JSON
    val response = service.fetchPokemonInfo("bulbasaur")

    // Extract data from ApiResponse.Success
    val responseBody = requireNotNull((response as ApiResponse.Success).data)

    // Verify fields from JSON are mapped correctly

    assertThat(responseBody.id, `is`(1))
    assertThat(responseBody.name, `is`("bulbasaur"))
    assertThat(responseBody.height, `is`(7))
    assertThat(responseBody.weight, `is`(69))
    assertThat(responseBody.experience, `is`(64))
  }
}