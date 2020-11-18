package argos.core.support

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

class FileSupportTest {

    @Test
    fun testJPGFromWeb() {
        val byteArray = FileSupport.getByteArrayFromFile("https://via.placeholder.com/150.jpg")
        Assertions.assertTrue(File("./src/test/resources/test.jpg").readBytes().contentEquals(byteArray))
    }

    @Test
    fun testWAV() {
        val byteArray = FileSupport.getByteArrayFromFile("./src/test/resources/placeholder.wav")
        Assertions.assertTrue(File("./src/test/resources/placeholder.wav").readBytes().contentEquals(byteArray))
    }
}