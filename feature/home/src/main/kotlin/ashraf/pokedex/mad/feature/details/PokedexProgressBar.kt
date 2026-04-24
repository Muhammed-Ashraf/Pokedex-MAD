package ashraf.pokedex.mad.feature.details

import android.content.res.Configuration
import androidx.annotation.FloatRange
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ashraf.pokedex.mad.core.designsystem.theme.PokedexTheme
import ashraf.pokedex.mad.core.designsystem.utils.pxToDp

@Composable
fun PokedexProgressBar(
    modifier: Modifier = Modifier,
    // value between 0 and 1
    @FloatRange(0.0, 1.0) progress: Float,
    color: Color,
    label: String,
) {
  // Get screen width (used only for preview fallback)
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp.value

  // True when rendering inside Android Studio Preview
  val isLocalInspectionMode = LocalInspectionMode.current

  // Holds the actual width of the progress bar (in px)
  // - In preview → set full width so it looks correct
  // - In real app → start from 0 and update later
  var progressWidth by remember {
    mutableFloatStateOf(
      if (isLocalInspectionMode) {
        screenWidth
      } else {
        0f
      },
    )
  }

  // Outer container (background bar)
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(18.dp)

      // Called after layout is measured
      // it.width = total width in px
      // progressWidth = totalWidth * progress
      .onSizeChanged { progressWidth = it.width * progress }

      // Background of progress bar (white rounded pill)
      .background(
        color = PokedexTheme.colors.absoluteWhite,
        shape = RoundedCornerShape(64.dp),
      )
      // ensure children are clipped to rounded shape
      .clip(RoundedCornerShape(64.dp)),
  ) {

    // Holds measured width of text (in px)
    var textWidth by remember { mutableIntStateOf(0) }

    // Minimum padding threshold to safely fit text inside
    val threshold = 16

    // Decide whether text should be inside the progress bar or outside
    // Condition:
    // progressWidth > textWidth + padding
    val isInner by remember(
      progressWidth,
      textWidth,
    ) { mutableStateOf(progressWidth > (textWidth + threshold * 2)) }

    // Animation value from 0 → 1
    // Instead of animating width directly, we multiply width by this value
    val animation: Float by animateFloatAsState(
      targetValue = if (progressWidth == 0f) 0f else 1f,
      animationSpec = tween(
        durationMillis = 950,
        easing = LinearOutSlowInEasing
      ),
      label = "",
    )

    // Inner progress fill bar
    Box(
      modifier = Modifier
        .align(Alignment.CenterStart)

        // Width grows from 0 → progressWidth using animation multiplier
        .width(
          progressWidth
            .toInt()
            .pxToDp() * animation,
        )
        .height(18.dp)
        .background(
          color = color,
          shape = RoundedCornerShape(64.dp),
        ),
    ) {

      // If enough space → place text INSIDE the bar
      if (isInner) {
        Text(
          modifier = Modifier

            // Measure text width dynamically
            .onSizeChanged { textWidth = it.width }

            // Align text to right end inside the bar
            .align(Alignment.CenterEnd)

            // Add padding so text doesn't stick to edge
            .padding(end = (threshold * 2).pxToDp()),

          text = label,
          fontSize = 12.sp,
          color = PokedexTheme.colors.absoluteWhite,
        )
      }
    }

    // If NOT enough space → place text OUTSIDE the bar
    if (!isInner) {
      Text(
        modifier = Modifier

          // Measure text width
          .onSizeChanged { textWidth = it.width }

          // Start placing from beginning of parent
          .align(Alignment.CenterStart)

          // Move text to the right of progress bar
          // = progressWidth + small gap
          .padding(
            start = progressWidth
              .toInt()
              .pxToDp() + threshold.pxToDp(),
          ),

        text = label,
        fontSize = 12.sp,
        color = PokedexTheme.colors.absoluteBlack,
      )
    }
  }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PokedexProgressBarPreview1() {
  // Preview wrapper with theme
  PokedexTheme {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
        .background(PokedexTheme.colors.background),
    ) {
      // Small progress → text likely OUTSIDE
      PokedexProgressBar(
        modifier = Modifier.align(Alignment.Center),
        progress = 0.1f,
        color = PokedexTheme.colors.primary,
        label = "150/300",
      )
    }
  }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PokedexProgressBarPreview2() {
  PokedexTheme {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
        .background(PokedexTheme.colors.background),
    ) {
      // Larger progress → text likely INSIDE
      PokedexProgressBar(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.Center),
        progress = 0.5f,
        color = PokedexTheme.colors.primary,
        label = "150/300",
      )
    }
  }
}