package ashraf.pokedex.mad.core.model

data class UserData(
    val uiTheme: UiTheme,
)

enum class UiTheme {
    FOLLOW_SYSTEM,
    DARK,
    LIGHT,
}
