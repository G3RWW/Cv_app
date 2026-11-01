package lt.gencevicius.cv.ui.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import lt.gencevicius.cv.R
import lt.gencevicius.cv.ui.common.composeEmail
import lt.gencevicius.cv.ui.common.openLink
import lt.gencevicius.cv.ui.theme.CelCard
import lt.gencevicius.cv.ui.theme.SectionHeader
import lt.gencevicius.cv.ui.theme.SpaceFox
import lt.gencevicius.cv.ui.theme.Tag
import java.io.File

private val TOOLS = listOf("Blender", "Maya", "Substance Painter", "Unity", "C#", "Figma")

class HomeActions(
    val openCv: () -> Unit,
    val openPortfolio: () -> Unit,
    val openShare: () -> Unit,
    val openContact: () -> Unit,
)

@Composable
fun HomeScreen(viewModel: HomeViewModel, actions: HomeActions) {
    val context = LocalContext.current
    val photoPath by viewModel.photoPath.collectAsStateWithLifecycle()

    // Survives the activity being destroyed while the camera app is in front.
    var pendingPhoto by rememberSaveable { mutableStateOf<String?>(null) }
    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        pendingPhoto?.let { viewModel.onPhotoResult(File(it), saved) }
        pendingPhoto = null
        if (!saved) Toast.makeText(context, R.string.error_photo_cancelled, Toast.LENGTH_SHORT).show()
    }
    val onTakePhoto = {
        val file = viewModel.newPhotoFile()
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        pendingPhoto = file.path
        try {
            takePicture.launch(uri)
        } catch (e: android.content.ActivityNotFoundException) {
            pendingPhoto = null
            Toast.makeText(context, R.string.error_no_camera, Toast.LENGTH_LONG).show()
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 600.dp && maxWidth > maxHeight
        if (wide) {
            // Landscape / tablet: identity on the left, about + actions on the right.
            Row(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Hero(
                    photoPath = photoPath,
                    photoSize = if (this@BoxWithConstraints.maxHeight < 480.dp) 112.dp else 176.dp,
                    onTakePhoto = onTakePhoto,
                    modifier = Modifier.weight(1f).widthIn(max = 460.dp).verticalScroll(rememberScrollState()),
                )
                Column(
                    Modifier.weight(1f).widthIn(max = 460.dp).verticalScroll(rememberScrollState()).padding(end = 6.dp, bottom = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    AboutCard()
                    ActionButtons(actions)
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                val narrow = Modifier.fillMaxWidth().widthIn(max = 560.dp)
                Hero(photoPath, if (this@BoxWithConstraints.maxWidth < 360.dp) 120.dp else 156.dp, onTakePhoto, narrow)
                Box(narrow.padding(end = 6.dp)) { AboutCard() }
                Box(narrow) { ActionButtons(actions) }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Hero(photoPath: String?, photoSize: Dp, onTakePhoto: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val email = stringResource(R.string.profile_email)
    val github = stringResource(R.string.profile_github)
    val artstation = stringResource(R.string.profile_artstation)
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box {
            // Photo inside a fox-orange → nebula-violet → star-cyan ring.
            Box(
                Modifier
                    .border(3.dp, Brush.sweepGradient(listOf(SpaceFox.Orange, SpaceFox.Violet, SpaceFox.Cyan, SpaceFox.Orange)), CircleShape)
                    .padding(6.dp),
            ) {
                val photoModifier = Modifier.size(photoSize).clip(CircleShape)
                val description = stringResource(R.string.profile_photo)
                if (photoPath == null) {
                    Image(painterResource(R.drawable.profile_placeholder), description, photoModifier, contentScale = ContentScale.Crop)
                } else {
                    AsyncImage(
                        model = File(photoPath),
                        contentDescription = description,
                        modifier = photoModifier,
                        placeholder = painterResource(R.drawable.profile_placeholder),
                        error = painterResource(R.drawable.profile_placeholder),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            FilledIconButton(
                onClick = onTakePhoto,
                modifier = Modifier.align(Alignment.BottomEnd),
                shape = MaterialTheme.shapes.small,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.action_change_photo))
            }
        }
        Spacer(Modifier.size(4.dp))
        Text(
            stringResource(R.string.profile_handle),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.tertiary,
        )
        Text(
            stringResource(R.string.profile_name),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.profile_title).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        FlowRow(
            Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            TOOLS.forEach { Tag(it) }
        }
        InfoRow(Icons.Default.Email, email) { context.composeEmail(email) }
        InfoRow(Icons.Default.Star, artstation) { context.openLink("https://www.$artstation") }
        InfoRow(Icons.Default.Build, github) { context.openLink("https://$github") }
        InfoRow(Icons.Default.LocationOn, stringResource(R.string.profile_location), onClick = null)
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String, onClick: (() -> Unit)?) {
    Row(
        Modifier
            .clip(MaterialTheme.shapes.small)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.width(10.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (onClick != null) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AboutCard() {
    CelCard(Modifier.fillMaxWidth(), accent = MaterialTheme.colorScheme.secondary) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(stringResource(R.string.home_about), color = MaterialTheme.colorScheme.secondary)
            Text(stringResource(R.string.profile_about), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ActionButtons(actions: HomeActions) {
    val colors = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ActionButton(Icons.Default.Person, R.string.home_view_cv, ButtonDefaults.buttonColors(colors.primary, colors.onPrimary), actions.openCv)
        ActionButton(Icons.Default.Star, R.string.home_view_portfolio, ButtonDefaults.buttonColors(colors.secondary, colors.onSecondary), actions.openPortfolio)
        ActionButton(Icons.Default.Share, R.string.home_share, ButtonDefaults.buttonColors(colors.tertiary, colors.onTertiary), actions.openShare)
        OutlinedButton(
            onClick = actions.openContact,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValuesTall,
        ) {
            ActionLabel(Icons.Default.Email, R.string.home_contact_me)
        }
    }
}

private val PaddingValuesTall = PaddingValues(horizontal = 24.dp, vertical = 14.dp)

@Composable
private fun ActionButton(icon: ImageVector, label: Int, colors: ButtonColors, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = colors,
        contentPadding = PaddingValuesTall,
    ) {
        ActionLabel(icon, label)
    }
}

@Composable
private fun ActionLabel(icon: ImageVector, label: Int) {
    Icon(icon, contentDescription = null, Modifier.size(ButtonDefaults.IconSize))
    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
    Text(stringResource(label).uppercase())
}
