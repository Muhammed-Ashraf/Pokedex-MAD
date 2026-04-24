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
package ashraf.pokedex.mad.core.database.entity.mapper

/**
 * Generic mapper between:
 *
 * - Domain model (used by core:model / core:data / UI)
 * - Entity model (used by core:database / Room)
 *
 * Why have this?
 * - Keeps Room types (entities) out of your domain layer.
 * - Makes it clear where the conversion logic lives.
 * - If the DB schema or domain model changes, you change mapping here in one place.
 *
 * In this project we mostly use it with:
 * - Domain = List<Pokemon>
 * - Entity = List<PokemonEntity>
 * via PokemonEntityMapper.
 */
interface EntityMapper<Domain, Entity> {

  /**
   * Convert from domain model to persistence entity.
   * Example: List<Pokemon> -> List<PokemonEntity>
   */
  fun asEntity(domain: Domain): Entity

  /**
   * Convert from persistence entity back to domain model.
   * Example: List<PokemonEntity> -> List<Pokemon>
   */
  fun asDomain(entity: Entity): Domain
}
