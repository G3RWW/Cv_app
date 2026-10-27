package lt.gencevicius.cv.ui.common

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.core.os.LocaleListCompat
import lt.gencevicius.cv.R

/**
 * Top bar button showing the current language (LT / EN) with a menu to switch it.
 * AppCompat stores the choice and recreates the activity with the new strings.
 */
@Composable
fun LanguageMenu() {
    var expanded by remember { mutableStateOf(false) }
    val shownLanguage = LocalConfiguration.current.locales[0].language
    val chosenTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    val menuDescription = stringResource(R.string.language_menu)

    Box {
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier.semantics { contentDescription = menuDescription },
        ) {
            Text(shownLanguage.uppercase())
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf(
                "lt" to R.string.language_lt,
                "en" to R.string.language_en,
                "" to R.string.language_system,
            ).forEach { (tag, label) ->
                DropdownMenuItem(
                    text = { Text(stringResource(label)) },
                    trailingIcon = if (tag == chosenTag) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null,
                    onClick = {
                        expanded = false
                        AppCompatDelegate.setApplicationLocales(
                            if (tag.isEmpty()) LocaleListCompat.getEmptyLocaleList()
                            else LocaleListCompat.forLanguageTags(tag),
                        )
                    },
                )
            }
        }
    }
}
