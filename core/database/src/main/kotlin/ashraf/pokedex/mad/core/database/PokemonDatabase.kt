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
