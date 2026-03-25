package ashraf.pokedx.mad.core.viewmodel

/**
 * Key used to distinguish state flows for different ViewModel types.
 *
 * Why this exists:
 * - When you keep multiple ViewModels alive (e.g. in Navigation back stack),
 *   you want a stable identifier per ViewModel type/class.
 * - The custom [ViewModelStateFlow] stores a map internally keyed by this value,
 *   so only the correct state slice is exposed for a given ViewModel instance/type.
 */
data class ViewModelKey(val key: String)