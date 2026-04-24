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
package ashraf.pokedex.mad.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ashraf.pokedex.mad.core.database.entity.PokemonEntity
import ashraf.pokedex.mad.core.database.entity.PokemonInfoEntity

/**
 * RoomDatabase = the main entry point to your Room DB.
 *
 * It ties together:
 * - Entities (tables)
 * - DAOs (queries)
 *
 * version:
 * - Start at 1.
 * - Each schema change increments version.
 *
 * exportSchema = true:
 * - Room will export schema JSON files into core/database/schemas/ (because we configured
 *   room.schemaLocation in build.gradle.kts).
 * - This helps with migrations later (and mirrors reference project).
 */
@Database(
  entities = [PokemonEntity::class, PokemonInfoEntity::class],
  version = 2,
  exportSchema = true,
)
@TypeConverters(value = [TypeResponseConverter::class, StatsResponseConverter::class])
abstract class PokedexDatabase : RoomDatabase() {

  abstract fun pokemonDao(): PokemonDao
  abstract fun pokemonInfoDao(): PokemonInfoDao
}
