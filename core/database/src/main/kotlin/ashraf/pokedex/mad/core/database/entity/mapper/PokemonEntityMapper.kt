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

import ashraf.pokedex.mad.core.database.entity.PokemonEntity
import ashraf.pokedex.mad.core.model.Pokemon

/**
 * Maps between:
 * - Domain list: List<Pokemon>   (core:model)
 * - Entity list: List<PokemonEntity> (core:database / Room)
 *
 * This keeps Room-specific types out of the rest of the app.
 * Repositories in core:data should work with Pokemon (domain),
 * and call these helpers when reading/writing the database.
 */
object PokemonEntityMapper : EntityMapper<List<Pokemon>, List<PokemonEntity>> {

  /**
   * Domain -> Entity
   *
   * Called before saving to DB.
   * We copy the fields we care about into PokemonEntity.
   */
  override fun asEntity(domain: List<Pokemon>): List<PokemonEntity> =
    domain.map { pokemon ->
      PokemonEntity(
        page = pokemon.page,
        // Domain model uses nameField for the raw API name; entity uses name as PK.
        name = pokemon.nameField,
        url = pokemon.url,
      )
    }

  /**
   * Entity -> Domain
   *
   * Called after reading from DB.
   * We reconstruct Pokemon using the stored fields.
   */
  override fun asDomain(entity: List<PokemonEntity>): List<Pokemon> =
    entity.map { entityItem ->
      Pokemon(
        page = entityItem.page,
        nameField = entityItem.name,
        url = entityItem.url,
      )
    }
}

/**
 * Extension to turn a list of domain Pokemon into a list of entities.
 *
 * Usage in repositories / data layer:
 *   pokemonDao.insertPokemonList(pokemons.asEntity())
 */
fun List<Pokemon>.asEntity(): List<PokemonEntity> =
  PokemonEntityMapper.asEntity(this)

/**
 * Extension to turn a nullable list of entities into a list of domain Pokemon.
 *
 * Usage after DAO calls:
 *   val pokemons: List<Pokemon> = pokemonDao.getPokemonList(page).asDomain()
 */
fun List<PokemonEntity>?.asDomain(): List<Pokemon> =
  PokemonEntityMapper.asDomain(this.orEmpty())
