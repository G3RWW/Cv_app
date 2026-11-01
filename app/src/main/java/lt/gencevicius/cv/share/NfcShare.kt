package lt.gencevicius.cv.share

import android.nfc.NdefMessage

/**
 * The NDEF file served by [NdefHostApduService]. Set only while the Share screen is open
 * and the app is in the foreground; otherwise the phone does not act as a tag.
 */
object NfcShare {
    @Volatile
    var ndefFile: ByteArray? = null
        private set

    fun publish(message: NdefMessage) {
        val bytes = message.toByteArray()
        // NDEF file = 2-byte length (NLEN) + message.
        ndefFile = byteArrayOf((bytes.size shr 8).toByte(), bytes.size.toByte()) + bytes
    }

    fun clear() {
        ndefFile = null
    }
}
