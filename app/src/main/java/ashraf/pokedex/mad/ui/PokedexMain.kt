package ashraf.pokedex.mad.ui

import androidx.compose.runtime.Composable
import ashraf.pokedex.mad.core.designsystem.theme.PokedexTheme
import ashraf.pokedex.mad.navigation.PokedexNavHost
import com.skydoves.compose.stability.runtime.TraceRecomposition

@Composable
@TraceRecomposition
fun PokedexMain(darkTheme: Boolean) {
  PokedexTheme(darkTheme = darkTheme) {
    PokedexNavHost()
  }
}