package ashraf.pokedex.mad.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ashraf.pokedex.mad.core.database.entity.PokemonEntity

/**
 * DAO = Data Access Object.
 *
 * This is the only place that should contain SQL queries.
 * Everything else (repositories, ViewModels) calls DAO methods, not raw SQL.
 */
@Dao
interface PokemonDao {

    /**
     * Insert a whole page worth of Pokemon rows.
     *
     * REPLACE means: if the same primary key (name) already exists, overwrite it.
     * This is useful when the network refetches and you want the cache updated.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonList(pokemonList: List<PokemonEntity>)

    /**
     * Read exactly one page of pokemon from cache.
     *
     * This mirrors the reference method name and query shape.
     */
    @Query("SELECT * FROM pokemon WHERE page = :page_ ORDER BY name")
    suspend fun getPokemonList(page_: Int): List<PokemonEntity>

    /**
     * Read all cached pages up to the given page (inclusive).
     *
     * The reference uses this pattern so the UI can show:
     * page 0 + page 1 + ... + page N as one growing list.
     */
    @Query("SELECT * FROM pokemon WHERE page <= :page_ ORDER BY page, name")
    suspend fun getAllPokemonList(page_: Int): List<PokemonEntity>
}