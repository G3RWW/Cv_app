package lt.gencevicius.cv.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import lt.gencevicius.cv.R

/**
 * Outlined text field that shows "Required" (or [errorText]) once the form was submitted
 * with this field blank or invalid.
 */
@Composable
fun RequiredTextField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes label: Int,
    required: Boolean = true,
    showError: Boolean = false,
    singleLine: Boolean = true,
    isValid: (String) -> Boolean = { !required || it.isNotBlank() },
    @StringRes errorText: Int = R.string.field_required,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val error = showError && !isValid(value)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) },
        isError = error,
        supportingText = if (error) {
            { Text(stringResource(errorText)) }
        } else null,
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        keyboardOptions = keyboardOptions,
        modifier = Modifier.fillMaxWidth(),
    )
}
