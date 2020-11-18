package argos.core.support

import java.io.File
import java.net.MalformedURLException
import java.net.URI
import java.net.URL

class FileSupport private constructor() {
    companion object {
        fun getByteArrayFromFile(fileURL: String): ByteArray {
            return try {
                URL(URI(fileURL).toASCIIString()).readBytes()
            } catch (ex: MalformedURLException) {
                URL(File(fileURL).toURI().toASCIIString()).readBytes()
            }
        }
    }
}