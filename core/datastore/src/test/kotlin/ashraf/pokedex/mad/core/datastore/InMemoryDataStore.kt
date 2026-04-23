package ashraf.pokedex.mad.core.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet

/**
 * In-memory implementation of DataStore.
 *
 * Used mainly for:
 * - Unit tests
 * - Fake repositories
 * - Avoiding disk I/O (no persistence)
 *
 * Data is stored only in RAM using StateFlow.
 */
class InMemoryDataStore<T>(initialValue: T) : DataStore<T> {

    /**
     * Backing storage for the DataStore.
     *
     * MutableStateFlow is used because:
     * - It holds a current value
     * - It emits updates to collectors
     * - It behaves like a reactive data holder
     *
     * So this replaces file-based storage with in-memory storage.
     */
    override val data = MutableStateFlow(initialValue)

    /**
     * Updates the stored value atomically.
     *
     * transform: a function that takes the current value
     * and returns a new value.
     *
     * Example:
     *   current = 10
     *   transform = { it + 5 }
     *   result = 15
     *
     * updateAndGet ensures:
     * - Thread-safe update
     * - Returns the new updated value
     */
    override suspend fun updateData(transform: suspend (T) -> T): T =
        data.updateAndGet { currentValue ->
            // Apply transformation to current value
            transform(currentValue)
        }
}