package lt.gencevicius.cv.ui.portfolio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import lt.gencevicius.cv.R

/**
 * Portfolio image downloaded from [url] with Coil (memory + disk cached).
 * While loading: placeholder image + spinner. On failure (no connection, bad URL):
 * error image + "Try again" button, which starts a fresh request.
 */
@Composable
fun PortfolioImage(url: String, contentDescription: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var attempt by rememberSaveable(url) { mutableIntStateOf(0) }
    val background = MaterialTheme.colorScheme.surfaceVariant

    // A new key throws away the failed request so the image is loaded again.
    key(url, attempt) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context).data(url).crossfade(true).build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier,
            loading = {
                Box(Modifier.fillMaxSize().background(background), contentAlignment = Alignment.Center) {
                    Image(painterResource(R.drawable.image_placeholder), contentDescription = null)
                    CircularProgressIndicator(Modifier.size(32.dp))
                }
            },
            error = {
                Column(
                    Modifier.fillMaxSize().background(background),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                ) {
                    Image(painterResource(R.drawable.image_error), contentDescription = null, Modifier.size(48.dp))
                    Text(stringResource(R.string.portfolio_image_failed), style = MaterialTheme.typography.bodySmall)
                    FilledTonalButton(onClick = { attempt++ }) {
                        Icon(Icons.Default.Refresh, contentDescription = null, Modifier.size(ButtonDefaults.IconSize))
                        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                        Text(stringResource(R.string.action_retry))
                    }
                }
            },
        )
    }
}
