package argos.core.support

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

class ImageSupportTest {

    @Test
    fun testGetByteArrayFromImage() {
        val imageByte = ImageSupport.getByteArrayFromImage("https://via.placeholder.com/150.jpg")
        val bufferedImage = ImageIO.read(ByteArrayInputStream(imageByte))!!

        Assertions.assertEquals(-3355444, bufferedImage.getRGB(0,0))
    }
}