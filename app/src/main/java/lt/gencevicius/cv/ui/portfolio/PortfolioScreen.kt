package lt.gencevicius.cv.ui.portfolio

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lt.gencevicius.cv.R
import lt.gencevicius.cv.data.DefaultContent
import lt.gencevicius.cv.data.PortfolioItem
import lt.gencevicius.cv.ui.common.ConfirmDeleteDialog
import lt.gencevicius.cv.ui.common.RequiredTextField
import lt.gencevicius.cv.ui.common.openLink

@Composable
fun PortfolioScreen(viewModel: PortfolioViewModel) {
    val userItems by viewModel.userItems.collectAsStateWithLifecycle()
    val builtIn = DefaultContent.portfolio.mapIndexed { index, item ->
        PortfolioItem(
            id = -(index + 1L),
            title = stringResource(item.title),
            description = stringResource(item.description),
            imageUrl = item.imageUrl,
            link = item.link,
        )
    }
    val items = builtIn + userItems

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var itemToDelete by rememberSaveable { mutableStateOf<Long?>(null) }

    Box(Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 280.dp),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items, key = { it.id }) { item ->
                PortfolioCard(item, onDelete = { itemToDelete = item.id })
            }
        }
        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text(stringResource(R.string.action_add)) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        )
    }

    if (showAddDialog) {
        AddPortfolioItemDialog(
            onSave = { viewModel.add(it); showAddDialog = false },
            onDismiss = { showAddDialog = false },
        )
    }
    userItems.firstOrNull { it.id == itemToDelete }?.let { item ->
        ConfirmDeleteDialog(
            itemTitle = item.title,
            onConfirm = { viewModel.delete(item); itemToDelete = null },
            onDismiss = { itemToDelete = null },
        )
    }
}

@Composable
private fun PortfolioCard(item: PortfolioItem, onDelete: () -> Unit) {
    val context = LocalContext.current
    Card(Modifier.fillMaxWidth()) {
        PortfolioImage(item.imageUrl, item.title, Modifier.fillMaxWidth().aspectRatio(16f / 9f))
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium)
            Text(item.description, style = MaterialTheme.typography.bodyMedium)
            if (item.isUserAdded) {
                Text(
                    stringResource(R.string.added_by_you),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            if (item.isUserAdded) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
                }
            }
            if (item.link.isNotBlank()) {
                TextButton(onClick = { context.openLink(item.link) }) {
                    Text(stringResource(R.string.action_open_link))
                }
            }
        }
    }
}

private fun isWebUrl(text: String) = text.startsWith("http") && Patterns.WEB_URL.matcher(text).matches()

@Composable
private fun AddPortfolioItemDialog(onSave: (PortfolioItem) -> Unit, onDismiss: () -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var imageUrl by rememberSaveable { mutableStateOf("") }
    var link by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }
    val urlKeyboard = KeyboardOptions(keyboardType = KeyboardType.Uri)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.portfolio_add_title)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RequiredTextField(title, { title = it }, R.string.field_title, showError = submitted)
                RequiredTextField(description, { description = it }, R.string.field_description, required = false, singleLine = false)
                RequiredTextField(
                    imageUrl, { imageUrl = it.trim() }, R.string.field_image_url,
                    showError = submitted, isValid = ::isWebUrl, errorText = R.string.field_invalid_url, keyboardOptions = urlKeyboard,
                )
                RequiredTextField(
                    link, { link = it.trim() }, R.string.field_link,
                    showError = submitted, isValid = { it.isEmpty() || isWebUrl(it) }, errorText = R.string.field_invalid_url,
                    keyboardOptions = urlKeyboard,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                submitted = true
                if (title.isNotBlank() && isWebUrl(imageUrl) && (link.isEmpty() || isWebUrl(link))) {
                    onSave(PortfolioItem(title = title.trim(), description = description.trim(), imageUrl = imageUrl, link = link))
                }
            }) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } },
    )
}
