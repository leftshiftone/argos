package argos.runtime.xml

import argos.core.assertion.IntentAssertion
import argos.core.assertion.SimilarityAssertion
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream

class ArgosXMLTest {
    val intentXmlFile: File = File("./src/test/resources/intentAssertionTest.xml")
    val similarityXmlFile: File = File("./src/test/resources/similarityAssertionTest.xml")

    @Test
    fun testIntentAssertions() {
        val parsed = ArgosXML().parse(FileInputStream(intentXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.assertionList

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(4, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is IntentAssertion) {
                Assertions.assertEquals("ich suche einen anwalt", assertion.spec.text)
                Assertions.assertEquals("findLawyer", assertion.spec.intent)
            }
        }
    }

    @Test
    fun testSimilarityAssertions() {
        val parsed = ArgosXML().parse(FileInputStream(similarityXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.assertionList

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is SimilarityAssertion) {
                Assertions.assertEquals("Der erste Text", assertion.spec.text1)
                Assertions.assertEquals("Der zweite Text", assertion.spec.text2)
                Assertions.assertEquals(0.9f, assertion.spec.threshold)
            }
        }
    }
}