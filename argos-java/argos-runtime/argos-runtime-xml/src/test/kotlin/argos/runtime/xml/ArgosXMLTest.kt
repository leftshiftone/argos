package argos.runtime.xml

import argos.core.assertion.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream

class ArgosXMLTest {
    val resourcesPath = File("./src/test/resources/")
    val intentXmlFile: File = File(resourcesPath, "intentAssertionTest.xml")
    val similarityXmlFile: File = File(resourcesPath, "similarityAssertionTest.xml")
    val nerXmlFile: File = File(resourcesPath, "nerAssertionTest.xml")
    val translationXmlFile = File(resourcesPath, "translationAssertionTest.xml")
    val sentimentXmlFile = File(resourcesPath, "sentimentAssertionTest.xml")
    val imageSimilarityXmlFile = File(resourcesPath, "imageSimilarityAssertionTest.xml")
    val conversationXmlFile = File(resourcesPath, "conversationAssertionTest.xml")
    val ocrXmlFile = File(resourcesPath, "ocrAssertionTest.xml")
    val languageDetectionXmlFile = File(resourcesPath, "languageDetectionAssertionTest.xml")
    val classificationXmlFile = File(resourcesPath, "classificationAssertionTest.xml")
    val regressionXmlFile = File(resourcesPath, "regressionAssertionTest.xml")

    @Test
    fun testIntentAssertions() {
        val parsed = ArgosXML.parse(FileInputStream(intentXmlFile))

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
        val parsed = ArgosXML.parse(FileInputStream(similarityXmlFile))

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
        val parsed = ArgosXML.parse(FileInputStream(nerXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.assertionList

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is NERAssertion) {
                val entities = assertion.spec.entities

                Assertions.assertEquals(2, assertion.spec.entities.size)

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

    @Test
    fun testTranslationAssertions() {
        val parsed = ArgosXML.parse(FileInputStream(translationXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.assertionList

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if(assertion is TranslationAssertion) {
                Assertions.assertEquals(0.9f, assertion.spec.threshold)

                Assertions.assertEquals("ich suche einen anwalt", assertion.spec.inText)
                Assertions.assertEquals("de", assertion.spec.inLang)

                Assertions.assertEquals("i am looking for a lawyer", assertion.spec.translatedText)
                Assertions.assertEquals("en", assertion.spec.translationLang)
            }
        }
    }

    @Test
    fun testSentimentAssertion() {
        val parsed = ArgosXML.parse(FileInputStream(sentimentXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.assertionList

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is SentimentAssertion) {
                Assertions.assertEquals("ich suche einen anwalt", assertion.spec.text)
                Assertions.assertEquals("neutral", assertion.spec.type)
            }
        }
    }

    @Test
    fun testImageSimilarityAssertion() {
        val parsed = ArgosXML.parse(FileInputStream(imageSimilarityXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.assertionList

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is ImageSimilarityAssertion) {
                Assertions.assertEquals("url1", assertion.spec.image1)
                Assertions.assertEquals("url2", assertion.spec.image2)
                Assertions.assertEquals(0.9f, assertion.spec.threshold)
            }
        }
    }

    @Test
    fun testOCRAssertion() {
        val parsed = ArgosXML.parse(FileInputStream(ocrXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.assertionList

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is OCRAssertion) {
                Assertions.assertEquals("url_to_image", assertion.spec.image)
                Assertions.assertEquals(false, assertion.spec.texts[0].fuzzy)
                Assertions.assertEquals("Das ist ein Beispieltext.", assertion.spec.texts[0].text)
                Assertions.assertEquals(true, assertion.spec.texts[1].fuzzy)
                Assertions.assertEquals("Das ist weiterer Beispieltext.", assertion.spec.texts[1].text)
            }
        }
    }

    @Test
    fun testLanguageDetectionAssertion() {
        val parsed = ArgosXML.parse(FileInputStream(languageDetectionXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.assertionList

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is LanguageDetectionAssertion) {
                Assertions.assertEquals("de", assertion.spec.lang)
                Assertions.assertEquals("Das ist ein Beispieltext", assertion.spec.text)
            }
        }
    }

    @Test
    fun testClassificationAssertion() {
        val parsed = ArgosXML.parse(FileInputStream(classificationXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.assertionList

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is ClassificationAssertion) {
                Assertions.assertEquals("Text", assertion.spec.text)
                Assertions.assertEquals("customClass", assertion.spec.`class`)
            }
        }
    }

    @Test
    fun testRegressionAssertion() {
        val parsed = ArgosXML.parse(FileInputStream(regressionXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.assertionList

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is RegressionAssertion) {
                Assertions.assertEquals("Text", assertion.spec.text)
                Assertions.assertEquals(90f, assertion.spec.score)
            }
        }
    }
}