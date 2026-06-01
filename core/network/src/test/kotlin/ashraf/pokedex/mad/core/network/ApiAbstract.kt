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
package ashraf.pokedex.mad.core.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import ashraf.pokedex.mad.core.test.MainCoroutinesRule
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.nio.charset.StandardCharsets

/**
 * Shared JUnit setup for Retrofit + Sandwich tests against a local [MockWebServer].
 *
 * Subclass as `ApiAbstract<PokedexService>()` and call [createService] in `@Before`.
 * JSON bodies live under `src/test/resources/api-response/` (see [enqueueResponse]).
 */
@RunWith(JUnit4::class)
abstract class ApiAbstract<T> {

  // Runs architecture-related background work synchronously in JVM unit tests.
  @Rule
  @JvmField
  val instantExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

  // Replaces Dispatchers.Main with a test dispatcher for the duration of each @Test.
  @get:Rule
  val coroutinesRule = MainCoroutinesRule()

  // In-process HTTP server; Retrofit baseUrl points here so no real network is used.
  lateinit var mockWebServer: MockWebServer

  @Before
  fun mockServer() {
    mockWebServer = MockWebServer()
    mockWebServer.start()
  }

  @After
  fun stopServer() {
    mockWebServer.shutdown()
  }

  /** Enqueue a response body from `src/test/resources/api-response/[fileName]`. */
  fun enqueueResponse(fileName: String) {
    enqueueResponse(fileName, emptyMap())
  }

  /**
   * Loads JSON from the test classpath and queues it as the next HTTP response.
   *
   * @param fileName e.g. `"PokemonResponse.json"` — **no** leading `/` (avoids `api-response//...` in the path).
   * @param headers optional response headers (e.g. `Link`, pagination).
   */
  private fun enqueueResponse(fileName: String, headers: Map<String, String>) {
    // Classpath root is src/test/resources; path must match folder + file on disk.
    val inputStream =
      requireNotNull(javaClass.classLoader?.getResourceAsStream("api-response/$fileName")) {
        "Missing test resource: api-response/$fileName (check src/test/resources)"
      }
    val body = inputStream.source().buffer().readString(StandardCharsets.UTF_8)
    val mockResponse = MockResponse()
    for ((key, value) in headers) {
      mockResponse.addHeader(key, value)
    }
    mockWebServer.enqueue(mockResponse.setBody(body))
  }

  /**
   * Builds a [Retrofit] instance matching production wiring (Json + Sandwich), but:
   * - [baseUrl] targets [mockWebServer] instead of pokeapi.co.
   * - No custom OkHttpClient here (tests use Retrofit defaults unless you add one).
   *
   * [Json] matches [ashraf.pokedex.mad.core.network.di.NetworkModule.provideJson] so parsing behaves the same.
   */
  fun createService(clazz: Class<T>): T {
    val json = Json {
      ignoreUnknownKeys = true
    }
    return Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
      .addCallAdapterFactory(
        ApiResponseCallAdapterFactory.create(
          coroutineScope = coroutinesRule.testScope,
        ),
      )
      .build()
      .create(clazz)
  }
}
