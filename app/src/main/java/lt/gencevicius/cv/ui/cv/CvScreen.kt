package lt.gencevicius.cv.ui.cv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lt.gencevicius.cv.R
import lt.gencevicius.cv.data.CvEntry
import lt.gencevicius.cv.data.CvSection
import lt.gencevicius.cv.data.DefaultContent
import lt.gencevicius.cv.ui.common.ConfirmDeleteDialog
import lt.gencevicius.cv.ui.common.RequiredTextField
import lt.gencevicius.cv.ui.theme.CelCard
import lt.gencevicius.cv.ui.theme.SectionHeader
import lt.gencevicius.cv.ui.theme.Tag

@Composable
private fun CvSection.accent(): Color = when (this) {
    CvSection.EXPERIENCE -> MaterialTheme.colorScheme.primary
    CvSection.EDUCATION -> MaterialTheme.colorScheme.secondary
    CvSection.SKILLS -> MaterialTheme.colorScheme.tertiary
}

val CvSection.label: Int
    get() = when (this) {
        CvSection.EXPERIENCE -> R.string.cv_section_experience
        CvSection.EDUCATION -> R.string.cv_section_education
        CvSection.SKILLS -> R.string.cv_section_skills
    }

@Composable
fun CvScreen(viewModel: CvViewModel) {
    val userEntries by viewModel.userEntries.collectAsStateWithLifecycle()
    val builtIn = DefaultContent.cv.mapIndexed { index, item ->
        CvEntry(
            id = -(index + 1L),
            section = item.section,
            title = stringResource(item.title),
            organization = item.organization?.let { stringResource(it) }.orEmpty(),
            period = item.period?.let { stringResource(it) }.orEmpty(),
            description = stringResource(item.description),
        )
    }
    val bySection = (builtIn + userEntries).groupBy { it.section }

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var entryToDelete by rememberSaveable { mutableStateOf<Long?>(null) }

    Box(Modifier.fillMaxSize()) {
        // One column on phones, more on tablets / landscape.
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CvSection.entries.forEach { section ->
                val entries = bySection[section].orEmpty()
                if (entries.isEmpty()) return@forEach
                item(key = "header_$section", span = { GridItemSpan(maxLineSpan) }) {
                    SectionHeader(stringResource(section.label), Modifier.padding(top = 8.dp), color = section.accent())
                }
                items(entries, key = { it.id }) { entry ->
                    CvEntryCard(entry, section.accent(), onDelete = { entryToDelete = entry.id })
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text(stringResource(R.string.action_add).uppercase()) },
            shape = MaterialTheme.shapes.small,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        )
    }

    if (showAddDialog) {
        AddCvEntryDialog(
            onSave = { viewModel.add(it); showAddDialog = false },
            onDismiss = { showAddDialog = false },
        )
    }
    userEntries.firstOrNull { it.id == entryToDelete }?.let { entry ->
        ConfirmDeleteDialog(
            itemTitle = entry.title,
            onConfirm = { viewModel.delete(entry); entryToDelete = null },
            onDismiss = { entryToDelete = null },
        )
    }
}

@Composable
private fun CvEntryCard(entry: CvEntry, accent: Color, onDelete: () -> Unit) {
    CelCard(Modifier.fillMaxWidth().padding(end = 5.dp, bottom = 5.dp), accent = accent) {
        Row(Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (entry.period.isNotBlank()) {
                    Text(entry.period.uppercase(), style = MaterialTheme.typography.labelMedium, color = accent)
                }
                Text(entry.title, style = MaterialTheme.typography.titleMedium)
                if (entry.organization.isNotBlank()) {
                    Text(entry.organization, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (entry.description.isNotBlank()) {
                    Text(entry.description, style = MaterialTheme.typography.bodyMedium)
                }
                if (entry.isUserAdded) {
                    Tag(stringResource(R.string.added_by_you))
                }
            }
            if (entry.isUserAdded) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddCvEntryDialog(onSave: (CvEntry) -> Unit, onDismiss: () -> Unit) {
    var section by rememberSaveable { mutableStateOf(CvSection.EXPERIENCE) }
    var title by rememberSaveable { mutableStateOf("") }
    var organization by rememberSaveable { mutableStateOf("") }
    var period by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.cv_add_title)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.cv_section), style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CvSection.entries.forEach {
                        FilterChip(
                            selected = section == it,
                            onClick = { section = it },
                            label = { Text(stringResource(it.label)) },
                        )
                    }
                }
                RequiredTextField(title, { title = it }, R.string.field_title, showError = submitted)
                RequiredTextField(organization, { organization = it }, R.string.field_organization, required = false)
                RequiredTextField(period, { period = it }, R.string.field_period, required = false)
                RequiredTextField(description, { description = it }, R.string.field_description, required = false, singleLine = false)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                submitted = true
                if (title.isNotBlank()) {
                    onSave(CvEntry(section = section, title = title.trim(), organization = organization.trim(), period = period.trim(), description = description.trim()))
                }
            }) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } },
    )
}
