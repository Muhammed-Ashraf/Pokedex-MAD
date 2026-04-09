package ashraf.pokedex.mad.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import ashraf.pokedex.mad.core.designsystem.R
import ashraf.pokedex.mad.core.designsystem.theme.PokedexTheme

// onActionClick: () -> Unit is a lambda parameter:
//   - () means the lambda takes no arguments
//   - Unit means it returns nothing (like void in Java)
// You can call onActionClick() inside this composable, e.g., when a button is clicked
@Composable
fun PokedexAppBar(onActionClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                color = PokedexTheme.colors.absoluteWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors()
            .copy(containerColor = PokedexTheme.colors.primary),
        actions = {
            IconButton(onClick = onActionClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null, tint = PokedexTheme.colors.absoluteWhite
                )
            }
        }
    )
}

@Preview
@Composable
fun PokedexAppBarPreview() {
    PokedexTheme { PokedexAppBar(onActionClick = {})}
}