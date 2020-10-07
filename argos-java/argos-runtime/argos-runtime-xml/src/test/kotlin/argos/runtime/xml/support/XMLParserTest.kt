package argos.runtime.xml.support

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.FileInputStream

class XMLParserTest {

    @Test
    fun testInvoke() {
        val parser = XmlParser(FileInputStream("./src/main/resources/argos.xsd"))
        val doc = parser.invoke(FileInputStream("./src/test/resources/test.xml"))

        Assertions.assertEquals("ich suche einen anwalt",
                doc.getElementsByTagName("intentAssertion").item(0).textContent)
    }
}