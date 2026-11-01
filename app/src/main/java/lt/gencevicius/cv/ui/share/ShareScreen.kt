package lt.gencevicius.cv.ui.share

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.cardemulation.CardEmulation
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import lt.gencevicius.cv.R
import lt.gencevicius.cv.share.NdefHostApduService
import lt.gencevicius.cv.share.NfcShare
import lt.gencevicius.cv.share.ShareContent
import lt.gencevicius.cv.share.qrCodeBitmap
import lt.gencevicius.cv.ui.common.startExternal
import lt.gencevicius.cv.ui.theme.CelCard
import lt.gencevicius.cv.ui.theme.SectionHeader

const val PORTFOLIO_URL = "https://www.artstation.com/umbravulpes"

// The same vCard as a file served by the Cloudflare Worker (repo folder docs/, wrangler.jsonc).
// iPhones only act on links read over NFC, so they get this URL; docs/_headers makes it a
// text/vcard shown inline, so Safari previews the contact instead of offering a download.
const val VCARD_URL = "https://cv-app.dovydasgencevicius.workers.dev/dovydas.vcf"

private enum class ShareMode { CONTACT, PORTFOLIO }

private enum class NfcTarget { ANDROID, IPHONE }

private enum class NfcState { READY, OFF, UNSUPPORTED }

/**
 * Share the card with another phone: a QR code for its camera, and NFC (this phone acts
 * as an NFC tag while the screen is open). Contact = vCard, portfolio = ArtStation link.
 */
@Composable
fun ShareScreen() {
    val context = LocalContext.current
    var mode by rememberSaveable { mutableStateOf(ShareMode.CONTACT) }
    var nfcTarget by rememberSaveable { mutableStateOf(NfcTarget.ANDROID) }

    val firstName = stringResource(R.string.profile_first_name)
    val lastName = stringResource(R.string.profile_last_name)
    val title = stringResource(R.string.profile_title)
    val email = stringResource(R.string.profile_email)
    val city = stringResource(R.string.profile_city)
    val content: ShareContent = when (mode) {
        ShareMode.CONTACT -> ShareContent.Contact(firstName, lastName, title, email, PORTFOLIO_URL, city)
        ShareMode.PORTFOLIO -> ShareContent.Link(PORTFOLIO_URL)
    }
    val qr = remember(content.qrText) { qrCodeBitmap(content.qrText) }
    // QR cameras on both systems understand a vCard; over NFC only Android does.
    val nfcContent: ShareContent =
        if (mode == ShareMode.CONTACT && nfcTarget == NfcTarget.IPHONE) ShareContent.Link(VCARD_URL) else content

    // NFC: serve the content only while this screen is resumed, and ask the system to prefer
    // this app for the NDEF tag AID so no chooser appears.
    val nfcAdapter = remember { NfcAdapter.getDefaultAdapter(context) }
    val hasHce = remember { context.packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION) }
    var resumeCount by remember { mutableIntStateOf(0) }
    LifecycleResumeEffect(nfcContent) {
        resumeCount++
        NfcShare.publish(nfcContent.toNdefMessage())
        val activity = context as? Activity
        val cardEmulation = if (hasHce && nfcAdapter != null) CardEmulation.getInstance(nfcAdapter) else null
        val service = ComponentName(context, NdefHostApduService::class.java)
        runCatching { if (activity != null) cardEmulation?.setPreferredService(activity, service) }
        onPauseOrDispose {
            NfcShare.clear()
            runCatching { if (activity != null) cardEmulation?.unsetPreferredService(activity) }
        }
    }
    // Re-read on every resume, so coming back from the NFC settings updates the status.
    val nfcState = remember(resumeCount) {
        when {
            nfcAdapter == null || !hasHce -> NfcState.UNSUPPORTED
            !nfcAdapter.isEnabled -> NfcState.OFF
            else -> NfcState.READY
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 600.dp && maxWidth > maxHeight
        val modePicker = @Composable {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeChip(mode == ShareMode.CONTACT, R.string.share_contact) { mode = ShareMode.CONTACT }
                ModeChip(mode == ShareMode.PORTFOLIO, R.string.share_portfolio) { mode = ShareMode.PORTFOLIO }
            }
        }
        val qrHint = stringResource(if (mode == ShareMode.CONTACT) R.string.share_qr_hint_contact else R.string.share_qr_hint_portfolio)
        val qrCard = @Composable { modifier: Modifier ->
            CelCard(modifier) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader(stringResource(R.string.share_qr_title))
                    Image(
                        bitmap = qr,
                        contentDescription = qrHint,
                        filterQuality = FilterQuality.None,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(Color.White, MaterialTheme.shapes.small)
                            .padding(8.dp),
                    )
                    Text(qrHint, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        val nfcCard = @Composable { modifier: Modifier ->
            CelCard(modifier, accent = MaterialTheme.colorScheme.tertiary) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeader(stringResource(R.string.share_nfc_title), color = MaterialTheme.colorScheme.tertiary)
                    Text(
                        stringResource(
                            when (nfcState) {
                                NfcState.READY -> R.string.share_nfc_ready
                                NfcState.OFF -> R.string.share_nfc_off
                                NfcState.UNSUPPORTED -> R.string.share_nfc_unsupported
                            },
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    if (mode == ShareMode.CONTACT && nfcState != NfcState.UNSUPPORTED) {
                        Text(
                            stringResource(R.string.share_nfc_target).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ModeChip(nfcTarget == NfcTarget.ANDROID, R.string.share_nfc_android) { nfcTarget = NfcTarget.ANDROID }
                            ModeChip(nfcTarget == NfcTarget.IPHONE, R.string.share_nfc_iphone) { nfcTarget = NfcTarget.IPHONE }
                        }
                        if (nfcTarget == NfcTarget.IPHONE) {
                            Text(
                                stringResource(R.string.share_nfc_iphone_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    if (nfcState == NfcState.OFF) {
                        OutlinedButton(
                            onClick = { context.startExternal(Intent(Settings.ACTION_NFC_SETTINGS), R.string.share_nfc_unsupported) },
                            shape = MaterialTheme.shapes.small,
                        ) { Text(stringResource(R.string.share_nfc_settings)) }
                    }
                }
            }
        }

        if (wide) {
            Row(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            ) {
                Column(Modifier.weight(1f).widthIn(max = 420.dp).verticalScroll(rememberScrollState())) {
                    qrCard(Modifier.fillMaxWidth().widthIn(max = 360.dp))
                }
                Column(
                    Modifier.weight(1f).widthIn(max = 420.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Text(stringResource(R.string.share_intro), style = MaterialTheme.typography.bodyLarge)
                    modePicker()
                    nfcCard(Modifier.fillMaxWidth())
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                val narrow = Modifier.fillMaxWidth().widthIn(max = 420.dp)
                Text(stringResource(R.string.share_intro), style = MaterialTheme.typography.bodyLarge, modifier = narrow)
                Row(narrow) { modePicker() }
                qrCard(narrow)
                nfcCard(narrow)
            }
        }
    }
}

@Composable
private fun ModeChip(selected: Boolean, label: Int, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(stringResource(label)) },
        shape = MaterialTheme.shapes.small,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        ),
    )
}
