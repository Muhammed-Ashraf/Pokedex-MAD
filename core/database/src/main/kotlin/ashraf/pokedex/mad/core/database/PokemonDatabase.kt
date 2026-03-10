package ashraf.pokedex.mad.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ashraf.pokedex.mad.core.database.entity.PokemonEntity

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
    entities = [PokemonEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class PokedexDatabase : RoomDatabase() {

    /**
     * Room generates the implementation for this at build time.
     * You never implement this yourself.
     */
    abstract fun pokemonDao(): PokemonDao
}
