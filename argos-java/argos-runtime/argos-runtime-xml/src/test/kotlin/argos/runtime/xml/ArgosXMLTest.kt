package argos.runtime.xml

import argos.core.assertion.IntentAssertion
import argos.core.assertion.NERAssertion
import argos.core.assertion.SimilarityAssertion
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream

class ArgosXMLTest {
    val resourcesPath = File("./src/test/resources/")
    val intentXmlFile: File = File(resourcesPath, "intentAssertionTest.xml")
    val similarityXmlFile: File = File(resourcesPath, "similarityAssertionTest.xml")
    val nerXmlFile: File = File(resourcesPath, "nerAssertionTest.xml")

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

    @Test
    fun testNerAssertions() {
        val parsed = ArgosXML().parse(FileInputStream(nerXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.assertionList

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is NERAssertion) {
                val entities = assertion.spec.entities

                Assertions.assertEquals("ich suche einen anwalt in der steiermark", assertion.spec.text)

                Assertions.assertEquals("steiermark", entities[0].text)
                Assertions.assertEquals("location", entities[0].label)
                Assertions.assertEquals(6, entities[0].index)
                Assertions.assertFalse(entities[0].not)

                Assertions.assertEquals("", entities[1].text)
                Assertions.assertEquals("organization", entities[1].label)
                Assertions.assertEquals(null, entities[1].index)
                Assertions.assertTrue(entities[1].not)
            }
        }
    }
}