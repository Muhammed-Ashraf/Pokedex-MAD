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
package ashraf.pokedex.mad.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity = one row in a SQLite table.
 *
 * We cache the Pokemon *list items* here (not the whole detail screen data yet).
 * This is an "offline-first" building block:
 * - network gets data from PokeAPI
 * - database stores it
 * - UI later reads from database via core:data repository
 *
 * Why separate Entity from core:model.Pokemon?
 * - Room entities are persistence-specific (table name, primary key, schema changes).
 * - Domain models are app/business-specific (used across layers).
 * We keep the database schema in core:database, and map it to core:model via a mapper (step 4.2.5).
 */
@Entity(tableName = "pokemon")
data class PokemonEntity(
  /**
   * Which pagination page this item belongs to.
   * Not from the API directly; we compute and store it so queries can fetch "page 0", "page 1", etc.
   */
  val page: Int = 0,

  /**
   * Primary key means "unique identifier for a row".
   * The reference uses name as PK because PokeAPI name is stable and unique (e.g. "bulbasaur").
   * Alternative would be an id extracted from url, but we'll mirror reference for now.
   */
  @PrimaryKey val name: String,

  /**
   * The PokeAPI url for this pokemon list item:
   * e.g. https://pokeapi.co/api/v2/pokemon/1/
   */
  val url: String,
)
