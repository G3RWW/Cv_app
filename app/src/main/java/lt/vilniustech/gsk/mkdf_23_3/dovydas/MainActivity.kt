package lt.vilniustech.gsk.mkdf_23_3.dovydas

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import lt.vilniustech.gsk.mkdf_23_3.dovydas.ui.CvApp
import lt.vilniustech.gsk.mkdf_23_3.dovydas.ui.theme.CvTheme

// AppCompatActivity (not ComponentActivity) so AppCompatDelegate.setApplicationLocales can
// switch the language at runtime on every Android version.
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // The app is always dark (space theme), so system bar icons are always light.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        val factory = (application as CvApplication).container.viewModelFactory
        setContent {
            CvTheme {
                CvApp(factory)
            }
        }
    }
}
