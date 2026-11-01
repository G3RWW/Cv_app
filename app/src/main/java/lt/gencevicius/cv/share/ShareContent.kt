package lt.gencevicius.cv.share

import android.nfc.NdefMessage
import android.nfc.NdefRecord

/** What the Share screen hands to another phone, as a QR code or over NFC. */
sealed interface ShareContent {
    /** Text encoded in the QR code. */
    val qrText: String

    /** The same content as an NDEF message for the emulated NFC tag. */
    fun toNdefMessage(): NdefMessage

    /** Opens the portfolio (ArtStation) in the other phone's browser. */
    data class Link(val url: String) : ShareContent {
        override val qrText get() = url
        override fun toNdefMessage() = NdefMessage(NdefRecord.createUri(url))
    }

    /** Offers to save the card owner as a new contact (vCard 3.0). */
    data class Contact(
        val firstName: String,
        val lastName: String,
        val title: String,
        val email: String,
        val url: String,
        val city: String,
    ) : ShareContent {
        override val qrText: String
            get() = listOf(
                "BEGIN:VCARD",
                "VERSION:3.0",
                "N:${escape(lastName)};${escape(firstName)};;;",
                "FN:${escape("$firstName $lastName")}",
                "TITLE:${escape(title)}",
                "EMAIL;TYPE=INTERNET:$email",
                "URL:$url",
                "ADR;TYPE=WORK:;;;${escape(city)};;;",
                "END:VCARD",
            ).joinToString("\r\n")

        override fun toNdefMessage() =
            NdefMessage(NdefRecord.createMime("text/vcard", qrText.toByteArray(Charsets.UTF_8)))

        private fun escape(value: String) =
            value.replace("\\", "\\\\").replace(",", "\\,").replace(";", "\;")
    }
}
