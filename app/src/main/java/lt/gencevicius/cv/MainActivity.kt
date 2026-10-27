package lt.gencevicius.cv

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import lt.gencevicius.cv.ui.CvApp
import lt.gencevicius.cv.ui.theme.CvTheme

// AppCompatActivity (not ComponentActivity) so AppCompatDelegate.setApplicationLocales can
// switch the language at runtime on every Android version.
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val factory = (application as CvApplication).container.viewModelFactory
        setContent {
            CvTheme {
                CvApp(factory)
            }
        }
    }
}
