package ashraf.pokedex.mad.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// =============================================================================
// POKEMON — List item model (Phase 3, Step 3.4)
// =============================================================================
// This is the shape of one item in the list from PokeAPI (e.g. GET .../pokemon).
// We use it in core:network (API response), core:database (cached row), and
// core:data (repository). So it lives in core:model and has no dependencies
// on Android UI, Room, or Retrofit.
// =============================================================================

/**
 * One Pokemon list entry. Matches the PokeAPI list response:
 * { "name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/" }
 *
 * @Serializable — Lets kotlinx.serialization encode/decode this class to JSON
 * (for Retrofit and for saving to disk). The compiler generates the serializer.
 */
@Serializable
data class Pokemon(
    /** 0-based page index when loading paginated list; not from API, we set it when building the list. */
    var page: Int = 0,

    /**
     * Name from API (lowercase, e.g. "bulbasaur").
     * We use @SerialName because the backing field is "nameField"; the JSON key is still "name".
     */
    @SerialName("name")
    val nameField: String,

    /** API URL for this Pokemon, e.g. "https://pokeapi.co/api/v2/pokemon/1/". */
    @SerialName("url")
    val url: String,
) {
    /**
     * Display name: first letter capitalized. Used in UI so we don't show "bulbasaur".
     */
    val name: String
        get() = nameField.replaceFirstChar { it.uppercase() }

    /**
     * Image URL derived from the API url. PokeAPI's "url" ends with the id (e.g. .../pokemon/1/);
     * the official artwork follows a fixed pattern. So we parse the id and build the image URL.
     */
    val imageUrl: String
        get() {
            val index = url.split("/").dropLast(1).lastOrNull() ?: "0"
            return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/" +
                "pokemon/other/official-artwork/$index.png"
        }
}
