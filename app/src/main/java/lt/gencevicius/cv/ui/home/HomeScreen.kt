package lt.gencevicius.cv.ui.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import java.io.File

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenCv: () -> Unit,
    onOpenPortfolio: () -> Unit,
    onOpenContact: () -> Unit,
) {
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
            // Landscape / tablet: card on the left, about + actions on the right.
            Row(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProfileCard(
                    photoPath = photoPath,
                    photoSize = if (this@BoxWithConstraints.maxHeight < 480.dp) 112.dp else 168.dp,
                    onTakePhoto = onTakePhoto,
                    modifier = Modifier.weight(1f).widthIn(max = 480.dp)
                        .verticalScroll(rememberScrollState()),
                )
                Column(
                    Modifier.weight(1f).widthIn(max = 480.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    AboutText()
                    HomeActions(onOpenCv, onOpenPortfolio, onOpenContact)
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val narrow = Modifier.fillMaxWidth().widthIn(max = 560.dp)
                ProfileCard(photoPath, if (this@BoxWithConstraints.maxWidth < 360.dp) 112.dp else 152.dp, onTakePhoto, narrow)
                Box(narrow) { AboutText() }
                Box(narrow) { HomeActions(onOpenCv, onOpenPortfolio, onOpenContact) }
            }
        }
    }
}

@Composable
private fun ProfileCard(photoPath: String?, photoSize: Dp, onTakePhoto: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val email = stringResource(R.string.profile_email)
    val github = stringResource(R.string.profile_github)
    Card(modifier) {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Surface(shape = CircleShape, border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary)) {
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
            TextButton(onClick = onTakePhoto) {
                Icon(Icons.Default.Edit, contentDescription = null, Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.action_change_photo))
            }
            Text(
                stringResource(R.string.profile_name),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.profile_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            ContactRow(Icons.Default.Email, email) { context.composeEmail(email) }
            ContactRow(Icons.Default.Share, github) { context.openLink("https://$github") }
            ContactRow(Icons.Default.LocationOn, stringResource(R.string.profile_location), onClick = null)
        }
    }
}

@Composable
private fun ContactRow(icon: ImageVector, text: String, onClick: (() -> Unit)?) {
    val content = @Composable {
        Icon(icon, contentDescription = null, Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
    if (onClick != null) {
        TextButton(onClick = onClick) { content() }
    } else {
        Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { content() }
    }
}

@Composable
private fun AboutText() {
    Text(stringResource(R.string.profile_about), style = MaterialTheme.typography.bodyLarge)
}

@Composable
private fun HomeActions(onOpenCv: () -> Unit, onOpenPortfolio: () -> Unit, onOpenContact: () -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onOpenCv, Modifier.fillMaxWidth()) {
            ActionLabel(Icons.Default.Person, R.string.home_view_cv)
        }
        FilledTonalButton(onClick = onOpenPortfolio, Modifier.fillMaxWidth()) {
            ActionLabel(Icons.Default.Star, R.string.home_view_portfolio)
        }
        OutlinedButton(onClick = onOpenContact, Modifier.fillMaxWidth()) {
            ActionLabel(Icons.Default.Email, R.string.home_contact_me)
        }
    }
}

@Composable
private fun ActionLabel(icon: ImageVector, label: Int) {
    Icon(icon, contentDescription = null, Modifier.size(ButtonDefaults.IconSize))
    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
    Text(stringResource(label))
}
