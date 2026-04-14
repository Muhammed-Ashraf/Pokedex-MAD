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