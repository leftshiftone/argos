package argos.core.assertion.support

import java.io.ByteArrayOutputStream
import java.net.URL
import javax.imageio.ImageIO

class ImageSupport private constructor() {
    companion object {
        fun getByteArrayFromImage(imageURL: String): ByteArray {
            val byteStream = ByteArrayOutputStream()
            val url = URL(imageURL)
            val extension = imageURL.substringAfterLast(".")

            val image = ImageIO.read(url)
            ImageIO.write(image, extension, byteStream)

            return byteStream.toByteArray()
        }
    }
}