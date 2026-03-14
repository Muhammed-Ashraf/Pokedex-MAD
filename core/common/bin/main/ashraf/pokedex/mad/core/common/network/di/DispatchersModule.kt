package ashraf.pokedex.mad.core.common.network.di

import ashraf.pokedex.mad.core.common.network.Dispatcher
import ashraf.pokedex.mad.core.common.network.PokedexAppDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Hilt module that provides CoroutineDispatchers.
 *
 * Why keep this in core:common:
 * - The dispatcher definitions are generic (do not depend on Android UI).
 * - Both core:network and core:data can reuse the same dispatcher injection.
 * - It centralizes where "which dispatcher" comes from instead of using
 *   Dispatchers.IO directly in many places.
 *
 * How it is used:
 * - Provides a CoroutineDispatcher qualified with @Dispatcher(IO).
 * - Any class that needs that dispatcher can inject:
 *   @Dispatcher(PokedexAppDispatchers.IO) dispatcher: CoroutineDispatcher
 */
@Module
@InstallIn(SingletonComponent::class)
internal object DispatchersModule {

    /**
     * Provides the IO dispatcher, qualified with @Dispatcher(IO).
     *
     * Why IO:
     * - IO dispatcher is optimized for offloading blocking IO tasks:
     *   network calls, database access, file reads/writes, etc.
     * - Most work in repositories and clients fits this pattern.
     */
    @Provides
    @Dispatcher(PokedexAppDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}