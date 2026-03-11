package ashraf.pokedex.mad.core.common.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * Qualifier used to distinguish *which* CoroutineDispatcher we want.
 *
 * Why this exists:
 * - We may use multiple dispatchers in an app:
 *   - IO (network, disk)
 *   - Default (CPU-heavy work)
 *   - Main (UI)
 * - Hilt can inject a CoroutineDispatcher, but without a qualifier
 *   it cannot know which one to provide.
 *
 * Usage:
 * - In a Hilt @Module we provide a dispatcher:
 *     @Provides
 *     @Dispatcher(PokedexAppDispatchers.IO)
 *     fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO
 *
 * - In classes that need this dispatcher we request:
 *     @Dispatcher(PokedexAppDispatchers.IO) dispatcher: CoroutineDispatcher
 */
@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val pokedexAppDispatchers: PokedexAppDispatchers)

/**
 * Marker enum for the different dispatcher "types" the app cares about.
 *
 * Right now:
 * - IO: background work (network, database, file IO).
 * The reference only uses IO here, but this pattern allows
 * adding other dispatcher types in the future without changing
 * the basic qualifier shape.
 */
enum class PokedexAppDispatchers {
    IO,
}