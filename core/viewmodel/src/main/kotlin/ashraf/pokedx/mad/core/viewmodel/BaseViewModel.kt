package ashraf.pokedx.mad.core.viewmodel

import androidx.lifecycle.ViewModel

/**
 * Base class for ViewModels in this reference-style architecture.
 *
 * Purpose:
 * - Provides a stable [ViewModelKey] for the ViewModel type.
 * - Provides helper to create a keyed [ViewModelStateFlow].
 *
 * Why it’s useful:
 * - It connects the ViewModel type -> [ViewModelKey] -> [ViewModelStateFlow] scoping.
 * - Helps keep state mutation safe and predictable.
 */
abstract class BaseViewModel : ViewModel() {

  /**
   * The key is derived from this ViewModel's class name.
   * That means different ViewModel types get different keys.
   */
  protected val key: ViewModelKey = ViewModelKey(this::class.java.name)

  /**
   * Helper to create a keyed [ViewModelStateFlow].
   *
   * Usage:
   *   private val state = viewModelStateFlow(InitialUiState(...))
   */
  protected fun <T> viewModelStateFlow(value: T): ViewModelStateFlow<T> {
    return ViewModelStateFlow(key = key, value = value)
  }
}