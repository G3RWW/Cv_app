package lt.gencevicius.cv.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// "Space fox" palette: deep-space background, fox orange, nebula violet, star cyan.
object SpaceFox {
    val Void = Color(0xFF0D0A1E)
    val Nebula = Color(0xFF160F33)
    val Orange = Color(0xFFFF8A3D)
    val Violet = Color(0xFF8F7CFF)
    val Cyan = Color(0xFF4FE3D6)
    val Star = Color(0xFFEDE8FF)
}

private val Colors = darkColorScheme(
    primary = SpaceFox.Orange,
    onPrimary = Color(0xFF2A1000),
    primaryContainer = Color(0xFF5A2A0E),
    onPrimaryContainer = Color(0xFFFFD9C2),
    secondary = SpaceFox.Violet,
    onSecondary = Color(0xFF170F3D),
    secondaryContainer = Color(0xFF2E2466),
    onSecondaryContainer = Color(0xFFE3DDFF),
    tertiary = SpaceFox.Cyan,
    onTertiary = Color(0xFF00201D),
    tertiaryContainer = Color(0xFF004F49),
    onTertiaryContainer = Color(0xFFB8FFF6),
    background = SpaceFox.Void,
    onBackground = SpaceFox.Star,
    surface = Color(0xFF17122E),
    onSurface = SpaceFox.Star,
    surfaceVariant = Color(0xFF221A42),
    onSurfaceVariant = Color(0xFFB5ACD6),
    surfaceContainerLowest = Color(0xFF0A0818),
    surfaceContainerLow = Color(0xFF140F29),
    surfaceContainer = Color(0xFF1A1434),
    surfaceContainerHigh = Color(0xFF221A42),
    surfaceContainerHighest = Color(0xFF2B2150),
    outline = Color(0xFF4A3F7A),
    outlineVariant = Color(0xFF2E2650),
    error = Color(0xFFFF6B81),
    onError = Color(0xFF3D0010),
)

// Cut corners instead of the usual rounded ones: a sharper, game-UI feel.
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
    medium = CutCornerShape(topStart = 18.dp, bottomEnd = 18.dp),
    large = CutCornerShape(topStart = 24.dp, bottomEnd = 24.dp),
    extraLarge = CutCornerShape(topStart = 28.dp, bottomEnd = 28.dp),
)

private val base = Typography()
private val AppTypography = Typography(
    headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.Black, letterSpacing = (-0.5).sp),
    headlineSmall = base.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
    titleLarge = base.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = base.titleMedium.copy(fontWeight = FontWeight.Bold),
    // Labels (buttons, navigation, tags) in monospace, like a terminal / game HUD.
    labelLarge = base.labelLarge.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    labelMedium = base.labelMedium.copy(fontFamily = FontFamily.Monospace, letterSpacing = 0.5.sp),
    labelSmall = base.labelSmall.copy(fontFamily = FontFamily.Monospace, letterSpacing = 1.sp),
)

/** Always the dark space theme: no dynamic colours, so the app keeps its own identity. */
@Composable
fun CvTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, shapes = AppShapes, typography = AppTypography, content = content)
}
