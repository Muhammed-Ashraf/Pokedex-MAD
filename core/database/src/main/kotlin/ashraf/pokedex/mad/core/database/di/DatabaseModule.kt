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
package ashraf.pokedex.mad.core.database.di

import android.app.Application
import androidx.room.Room
import ashraf.pokedex.mad.core.database.PokedexDatabase
import ashraf.pokedex.mad.core.database.PokemonDao
import ashraf.pokedex.mad.core.database.PokemonInfoDao
import ashraf.pokedex.mad.core.database.StatsResponseConverter
import ashraf.pokedex.mad.core.database.TypeResponseConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * Hilt module that tells Hilt:
 * - how to build the Room database
 * - how to get the DAO from that database
 *
 * After this, you can inject PokemonDao anywhere in the app
 * (via core:data repositories later).
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  /**
   * Builds the PokedexDatabase singleton.
   *
   * - Application: provided automatically by Hilt.
   * - databaseBuilder: standard Room pattern.
   * - fallbackToDestructiveMigration(): for now we drop+recreate on schema change
   */
  @Provides
  @Singleton
  fun providePokedexDatabase(
    application: Application,
    typeResponseConverter: TypeResponseConverter,
    statsResponseConverter: StatsResponseConverter,
  ): PokedexDatabase =
    Room.databaseBuilder(
      application,
      PokedexDatabase::class.java,
      "Pokedex.db",
    )
      .fallbackToDestructiveMigration()
      .addTypeConverter(typeResponseConverter)
      .addTypeConverter(statsResponseConverter)
      .build()

  /**
   * Exposes PokemonDao as a Hilt dependency.
   *
   * Any Hilt-constructed class can now `@Inject` a PokemonDao
   * (typically through a repository in core:data).
   */
  @Provides
  @Singleton
  fun providePokemonDao(
    database: PokedexDatabase,
  ): PokemonDao = database.pokemonDao()

  @Provides
  @Singleton
  fun providePokemonInfoDao(appDatabase: PokedexDatabase): PokemonInfoDao {
    return appDatabase.pokemonInfoDao()
  }

  @Provides
  @Singleton
  fun provideTypeResponseConverter(json: Json): TypeResponseConverter {
    return TypeResponseConverter(json)
  }

  @Provides
  @Singleton
  fun provideStatsResponseConverter(json: Json): StatsResponseConverter {
    return StatsResponseConverter(json)
  }
}
