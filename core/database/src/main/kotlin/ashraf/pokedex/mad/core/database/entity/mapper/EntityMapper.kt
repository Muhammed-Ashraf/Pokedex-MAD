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