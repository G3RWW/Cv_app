package lt.gencevicius.cv.ui.contact

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import lt.gencevicius.cv.R
import lt.gencevicius.cv.ui.common.RequiredTextField
import lt.gencevicius.cv.ui.common.composeEmail

private fun isEmail(text: String) = Patterns.EMAIL_ADDRESS.matcher(text.trim()).matches()

/**
 * "Contact me" form. Sending hands the message to the user's email app via an
 * ACTION_SENDTO intent, addressed to the card owner, so the app needs no server or account.
 */
@Composable
fun ContactScreen() {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var question by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }

    val ownerEmail = stringResource(R.string.profile_email)
    val subject = stringResource(R.string.contact_subject, name.trim())
    val body = stringResource(R.string.contact_body, name.trim(), email.trim(), phone.trim().ifEmpty { "–" }, question.trim())

    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            Modifier.fillMaxWidth().widthIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.contact_intro), style = MaterialTheme.typography.bodyLarge)
            RequiredTextField(
                name, { name = it }, R.string.contact_name, showError = submitted,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
            )
            RequiredTextField(
                email, { email = it }, R.string.contact_email, showError = submitted,
                isValid = ::isEmail, errorText = R.string.contact_error_email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            )
            RequiredTextField(
                phone, { phone = it }, R.string.contact_phone, required = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            )
            RequiredTextField(
                question, { question = it }, R.string.contact_question, showError = submitted, singleLine = false,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            )
            Text(
                stringResource(R.string.contact_hint_app),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = {
                    submitted = true
                    if (name.isNotBlank() && isEmail(email) && question.isNotBlank()) {
                        context.composeEmail(to = ownerEmail, subject = subject, body = body)
                    }
                },
                modifier = Modifier.align(Alignment.End),
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.action_send))
            }
        }
    }
}
