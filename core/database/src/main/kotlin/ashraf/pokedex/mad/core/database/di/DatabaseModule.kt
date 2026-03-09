
package ashraf.pokedex.mad.core.database.di

import android.app.Application
import androidx.room.Room
import ashraf.pokedex.mad.core.database.PokedexDatabase
import ashraf.pokedex.mad.core.database.PokemonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    ): PokedexDatabase =
        Room.databaseBuilder(
            application,
            PokedexDatabase::class.java,
            "Pokedex.db",
        )
            .fallbackToDestructiveMigration()
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
}