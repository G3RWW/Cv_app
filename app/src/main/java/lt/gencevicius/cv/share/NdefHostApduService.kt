package lt.gencevicius.cv.share

import android.nfc.cardemulation.HostApduService
import android.os.Bundle

/**
 * Host card emulation of an NFC Forum Type 4 Tag holding one read-only NDEF message.
 * Another phone that taps this one reads it like an ordinary NFC sticker: a URI record
 * opens the browser, a text/vcard record offers to add a contact. No app is needed there.
 */
class NdefHostApduService : HostApduService() {
    private enum class SelectedFile { NONE, CAPABILITY_CONTAINER, NDEF }

    private var applicationSelected = false
    private var selected = SelectedFile.NONE

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        val ndefFile = NfcShare.ndefFile ?: return SW_FILE_NOT_FOUND
        if (commandApdu.size < 4) return SW_WRONG_LENGTH
        val ins = commandApdu[1]
        val p1 = commandApdu[2].toInt() and 0xFF

        return when (ins) {
            INS_SELECT -> {
                val lc = if (commandApdu.size > 4) commandApdu[4].toInt() and 0xFF else 0
                if (commandApdu.size < 5 + lc) return SW_WRONG_LENGTH
                val data = commandApdu.copyOfRange(5, 5 + lc)
                when {
                    p1 == 0x04 && data.contentEquals(NDEF_APPLICATION_ID) -> {
                        applicationSelected = true
                        selected = SelectedFile.NONE
                        SW_OK
                    }
                    applicationSelected && p1 == 0x00 && data.contentEquals(CC_FILE_ID) -> {
                        selected = SelectedFile.CAPABILITY_CONTAINER
                        SW_OK
                    }
                    applicationSelected && p1 == 0x00 && data.contentEquals(NDEF_FILE_ID) -> {
                        selected = SelectedFile.NDEF
                        SW_OK
                    }
                    else -> SW_FILE_NOT_FOUND
                }
            }
            INS_READ_BINARY -> {
                val file = when (selected) {
                    SelectedFile.CAPABILITY_CONTAINER -> CAPABILITY_CONTAINER
                    SelectedFile.NDEF -> ndefFile
                    SelectedFile.NONE -> return SW_FILE_NOT_FOUND
                }
                val offset = (p1 shl 8) or (commandApdu[3].toInt() and 0xFF)
                val requested = if (commandApdu.size > 4) (commandApdu[4].toInt() and 0xFF).let { if (it == 0) 256 else it } else 256
                if (offset > file.size) return SW_WRONG_OFFSET
                file.copyOfRange(offset, minOf(file.size, offset + requested)) + SW_OK
            }
            else -> SW_INS_NOT_SUPPORTED
        }
    }

    override fun onDeactivated(reason: Int) {
        applicationSelected = false
        selected = SelectedFile.NONE
    }

    private companion object {
        const val INS_SELECT: Byte = 0xA4.toByte()
        const val INS_READ_BINARY: Byte = 0xB0.toByte()

        val NDEF_APPLICATION_ID = bytes(0xD2, 0x76, 0x00, 0x00, 0x85, 0x01, 0x01)
        val CC_FILE_ID = bytes(0xE1, 0x03)
        val NDEF_FILE_ID = bytes(0xE1, 0x04)

        // Capability container: mapping version 2.0, max read 255 bytes, NDEF file E104
        // of up to 2048 bytes, readable by anyone, never writable.
        val CAPABILITY_CONTAINER = bytes(
            0x00, 0x0F, 0x20, 0x00, 0xFF, 0x00, 0xFF,
            0x04, 0x06, 0xE1, 0x04, 0x08, 0x00, 0x00, 0xFF,
        )

        val SW_OK = bytes(0x90, 0x00)
        val SW_FILE_NOT_FOUND = bytes(0x6A, 0x82)
        val SW_WRONG_LENGTH = bytes(0x67, 0x00)
        val SW_WRONG_OFFSET = bytes(0x6B, 0x00)
        val SW_INS_NOT_SUPPORTED = bytes(0x6D, 0x00)

        fun bytes(vararg values: Int) = ByteArray(values.size) { values[it].toByte() }
    }
}
