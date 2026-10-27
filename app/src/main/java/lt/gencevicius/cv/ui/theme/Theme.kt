package lt.gencevicius.cv.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = Color(0xFF1F4E79),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4F0),
    onPrimaryContainer = Color(0xFF0B2239),
    secondary = Color(0xFF52606D),
    tertiary = Color(0xFF7A5900),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA6C8EC),
    onPrimary = Color(0xFF0B2239),
    primaryContainer = Color(0xFF1F4E79),
    onPrimaryContainer = Color(0xFFD6E4F0),
    secondary = Color(0xFFB9C7D4),
    tertiary = Color(0xFFF0C048),
)

@Composable
fun CvTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(colorScheme = colors, content = content)
}
