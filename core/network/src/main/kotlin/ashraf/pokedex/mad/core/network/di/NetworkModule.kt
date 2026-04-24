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
package ashraf.pokedex.mad.core.network.di

import ashraf.pokedex.mad.core.network.BuildConfig
import ashraf.pokedex.mad.core.network.service.PokedexService
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
  }

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
    .apply {
      if (BuildConfig.DEBUG) {
        addInterceptor(
          HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY),
        )
      }
    }
    .build()

  @Provides
  @Singleton
  fun provideRetrofit(
    json: Json,
    okHttpClient: OkHttpClient,
  ): Retrofit = Retrofit.Builder()
    .baseUrl("https://pokeapi.co/api/v2/")
    .client(okHttpClient)
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
    .build()

  @Provides
  @Singleton
  fun providePokedexService(retrofit: Retrofit): PokedexService =
    retrofit.create(PokedexService::class.java)
}
