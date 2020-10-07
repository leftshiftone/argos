package argos.runtime.xml

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.*

class ArgosXMLTest {
    val xmlFile: File = File("./src/test/resources/test.xml")

    @Test
    fun testParse() {
        val parsed = ArgosXML().parse(FileInputStream(xmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.intentAssertionList

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(4, assertionList.size)

        for (assertion in assertionList) {
            Assertions.assertEquals("ich suche einen anwalt", assertion.spec.text)
            Assertions.assertEquals("findLawyer", assertion.spec.intent)
        }
    }
}