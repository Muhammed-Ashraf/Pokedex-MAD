package ashraf.pokedex.mad.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ashraf.pokedex.mad.core.designsystem.theme.PokedexTheme

@Composable
fun BoxScope.PokedexCircularProgress() {
    // BoxScope means:
    // 👉 This composable can ONLY be called inside a Box {}
    // 👉 It gives access to Box-specific modifiers like align()

    CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center),
        // align() works because of BoxScope
        // This centers the loader inside the Box

        color = PokedexTheme.colors.primary
        // Uses the app's primary theme color for the spinner
    )
}

@Preview
@Composable
private fun PokedexCircularProgressPreview() {
    PokedexTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            PokedexCircularProgress()
        }
    }
}