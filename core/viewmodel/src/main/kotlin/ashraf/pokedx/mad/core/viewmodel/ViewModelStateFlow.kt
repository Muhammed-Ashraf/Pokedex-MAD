package ashraf.pokedx.mad.core.viewmodel

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A MutableStateFlow wrapper that is *keyed* to a specific ViewModel type.
 *
 * Why it’s more complex than a normal MutableStateFlow:
 * - It internally stores a map: Map<ViewModelKey, T>
 * - The exposed `value` and `emit/tryEmit/collect` operations are scoped to *one key*.
 *
 * The intention (reference-aligned):
 * - Prevent accidental cross-ViewModel state updates.
 * - Provide a pattern that works well when ViewModels are kept alive
 *   (for example by Navigation back stack).
 *
 * How to use:
 * - ViewModels should create it via [BaseViewModel.viewModelStateFlow].
 * - The ViewModel updates the state by using the custom `emit(key, value)` method.
 * - Call sites should read the state (as StateFlow) but not mutate it directly.
 */
class ViewModelStateFlow<T>(
  private val key: ViewModelKey,
  value: T,
) : MutableStateFlow<T> {

  // Internal storage: each key maps to its own state value.
  private val mutableStateFlow: MutableStateFlow<Map<ViewModelKey, T>> =
    MutableStateFlow(mapOf(key to value))

  override val subscriptionCount: StateFlow<Int>
    get() = mutableStateFlow.subscriptionCount

  /**
   * Scoped emit:
   * - You must emit with the same [ViewModelKey] that this state flow is bound to.
   * - If you pass a different key, this throws to catch bugs early.
   */
  suspend fun emit(key: ViewModelKey, value: T) {
    if (key != this.key) {
      throw IllegalArgumentException(
        "Used different key to emit new value: $value! " +
          "Don't manipulate key value or try to emit out of ViewModels",
      )
    }
    mutableStateFlow.emit(mapOf(key to value))
  }

  /**
   * Restricted override:
   * - Normal MutableStateFlow.emit(value) is blocked.
   * - The ViewModel should call the scoped emit(key, value) instead.
   */
  @RestrictedApi
  override suspend fun emit(value: T) =
    throw IllegalAccessError("Use `emitValue` function instead of this")

  /**
   * Scoped tryEmit:
   * - Works like [emit] but returns a Boolean.
   * - Still validates the key to keep state updates safe.
   */
  fun tryEmit(key: ViewModelKey, value: T): Boolean {
    if (key != this.key) {
      throw IllegalArgumentException(
        "Used different key to emit new value: $value! " +
          "Don't manipulate key value or try to emit out of ViewModels",
      )
    }

    return mutableStateFlow.tryEmit(mapOf(key to value))
  }

  /**
   * Restricted override:
   * - Blocks tryEmit(value) for the same reason as [emit(value)].
   */
  @RestrictedApi
  override fun tryEmit(value: T): Boolean =
    throw IllegalAccessError("Use `tryEmitValue` function instead of this")

  override fun resetReplayCache() = mutableStateFlow.resetReplayCache()

  /**
   * Exposed `value` property is scoped to this state flow's key.
   */
  override var value: T
    get() = mutableStateFlow.value.getValue(key)
    set(value) {
      mutableStateFlow.value = mapOf(key to value)
    }

  override fun compareAndSet(expect: T, update: T): Boolean =
    mutableStateFlow.compareAndSet(mapOf(key to expect), mapOf(key to update))

  override val replayCache: List<T>
    get() = mutableStateFlow.replayCache.map { it.getValue(key) }

  /**
   * Collect is scoped:
   * - Under the hood the internal state flow emits maps,
   * - but this collect() only emits the T value for this key.
   */
  override suspend fun collect(collector: FlowCollector<T>): Nothing =
    mutableStateFlow.collect { map ->
      collector.emit(map.getValue(key))
    }
}