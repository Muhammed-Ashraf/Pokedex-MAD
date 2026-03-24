package ashraf.pokedex.mad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ashraf.pokedex.mad.core.model.Pokemon
import ashraf.pokedex.mad.core.designsystem.theme.PokedexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokedexTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Step 3.5: Sample Pokemon from core:model to verify the dependency.
                    val samplePokemon = Pokemon(
                        page = 0,
                        nameField = "pikachu",
                        url = "https://pokeapi.co/api/v2/pokemon/25/",
                    )
                    Greeting(
                        name = samplePokemon.name,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun GreetingPreview() {
    PokedexTheme {
        val samplePokemon = Pokemon(
            page = 0,
            nameField = "bulbasaur",
            url = "https://pokeapi.co/api/v2/pokemon/1/",
        )
        Greeting(samplePokemon.name)
    }
}