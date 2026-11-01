package lt.gencevicius.cv.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private class StarDot(val x: Float, val y: Float, val radiusDp: Float, val alpha: Float)

/** Deep-space backdrop: gradient, two soft nebula glows and a fixed field of stars. */
@Composable
fun SpaceBackground(modifier: Modifier = Modifier) {
    val stars = remember {
        val random = Random(2134)
        List(140) { StarDot(random.nextFloat(), random.nextFloat(), 0.4f + random.nextFloat() * 1.3f, 0.25f + random.nextFloat() * 0.75f) }
    }
    Canvas(modifier) {
        drawRect(Brush.verticalGradient(listOf(SpaceFox.Void, SpaceFox.Nebula, SpaceFox.Void)))
        drawCircle(
            Brush.radialGradient(listOf(SpaceFox.Violet.copy(alpha = 0.22f), Color.Transparent), center = Offset(size.width * 0.9f, size.height * 0.1f), radius = size.maxDimension * 0.55f),
            radius = size.maxDimension * 0.55f, center = Offset(size.width * 0.9f, size.height * 0.1f),
        )
        drawCircle(
            Brush.radialGradient(listOf(SpaceFox.Orange.copy(alpha = 0.14f), Color.Transparent), center = Offset(size.width * 0.05f, size.height * 0.95f), radius = size.maxDimension * 0.5f),
            radius = size.maxDimension * 0.5f, center = Offset(size.width * 0.05f, size.height * 0.95f),
        )
        stars.forEach { star ->
            drawCircle(SpaceFox.Star.copy(alpha = star.alpha), radius = star.radiusDp.dp.toPx(), center = Offset(star.x * size.width, star.y * size.height))
        }
    }
}

/**
 * Card in a cel-shaded / comic style: flat fill, thin outline and a hard, offset
 * "shadow" block in the [accent] colour instead of a soft elevation shadow.
 */
@Composable
fun CelCard(
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable ColumnScope.() -> Unit,
) {
    val fill = MaterialTheme.colorScheme.surfaceContainer
    Column(
        modifier
            .drawBehind {
                val outline = shape.createOutline(size, layoutDirection, this)
                val offset = 5.dp.toPx()
                translate(offset, offset) { drawOutline(outline, accent) }
            }
            .background(fill, shape)
            .border(1.5.dp, accent.copy(alpha = 0.7f), shape)
            .clip(shape),
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) { content() }
    }
}

/** "// EXPERIENCE ———" style section title. */
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("// ${text.uppercase()}", style = MaterialTheme.typography.labelLarge, color = color)
        HorizontalDivider(Modifier.weight(1f), thickness = 1.dp, color = color.copy(alpha = 0.4f))
    }
}

/** Small outlined tag, e.g. a skill or "added by you". */
@Composable
fun Tag(text: String, color: Color = MaterialTheme.colorScheme.tertiary) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .border(1.dp, color.copy(alpha = 0.6f), MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

/** Accent colours cycled through list items. */
@Composable
fun accentFor(index: Int): Color = when (index % 3) {
    0 -> MaterialTheme.colorScheme.primary
    1 -> MaterialTheme.colorScheme.secondary
    else -> MaterialTheme.colorScheme.tertiary
}
