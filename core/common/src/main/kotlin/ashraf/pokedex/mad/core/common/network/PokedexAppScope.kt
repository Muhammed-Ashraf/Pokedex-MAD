package ashraf.pokedex.mad.core.common.network

import javax.inject.Qualifier

/**
 * Qualifier for the *application-wide* CoroutineScope.
 *
 * Why this exists:
 * - In a Hilt graph you can have multiple CoroutineScopes:
 *   - ViewModelScope
 *   - LifecycleOwner / screen scopes
 *   - Application scope
 * - Hilt needs a way to know *which* scope you are asking for.
 *
 * By annotating a provided CoroutineScope with @PokedexAppScope,
 * and also annotating an injected CoroutineScope parameter with @PokedexAppScope,
 * we ensure Hilt wires up the correct one (the app scope) instead of any other.
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class PokedexAppScope