package lt.gencevicius.cv.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes
import lt.gencevicius.cv.R

/** Starts [intent] in another app; shows [notFoundMessage] when no app can handle it. */
fun Context.startExternal(intent: Intent, @StringRes notFoundMessage: Int) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, notFoundMessage, Toast.LENGTH_LONG).show()
    }
}

fun Context.openLink(url: String) =
    startExternal(Intent(Intent.ACTION_VIEW, Uri.parse(url)), R.string.error_no_browser)

/** Opens an email app with [to], [subject] and [body] filled in; the user only taps Send. */
fun Context.composeEmail(to: String, subject: String = "", body: String = "") {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // only email apps handle mailto:
        putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    startExternal(intent, R.string.error_no_email_app)
}
