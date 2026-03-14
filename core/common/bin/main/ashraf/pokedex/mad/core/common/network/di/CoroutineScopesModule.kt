package ashraf.pokedex.mad.core.common.network.di

import ashraf.pokedex.mad.core.common.network.Dispatcher
import ashraf.pokedex.mad.core.common.network.PokedexAppDispatchers
import ashraf.pokedex.mad.core.common.network.PokedexAppScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Hilt module that provides application-level CoroutineScopes.
 *
 * Why an "app scope":
 * - Some coroutines should outlive a single screen or ViewModel:
 *   - prefetching data
 *   - long-running sync tasks
 *   - work that should continue while the app is in the foreground
 * - Instead of creating many ad-hoc scopes, we define ONE app-wide scope
 *   and inject it where long-lived work is appropriate.
 *
 * Design choices:
 * - Uses SupervisorJob(): if one child coroutine fails, it does NOT cancel
 *   all other coroutines in this scope.
 * - Uses the IO dispatcher by default (via @Dispatcher(IO)), which is suitable
 *   for most background tasks (network/DB).
 */
@Module
@InstallIn(SingletonComponent::class)
internal object CoroutineScopesModule {

    /**
     * Provides a single application-wide CoroutineScope.
     *
     * Qualifiers:
     * - @Singleton: only one instance for the entire app.
     * - @PokedexAppScope: identifies this particular scope in the Hilt graph.
     *
     * The scope is built as:
     *   CoroutineScope(SupervisorJob() + dispatcher)
     * where 'dispatcher' is the IO dispatcher from DispatchersModule.
     */
    @Provides
    @Singleton
    @PokedexAppScope
    fun providesCoroutineScope(
        @Dispatcher(PokedexAppDispatchers.IO) dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}