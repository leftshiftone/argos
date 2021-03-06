package argos.runtime.xml

import argos.core.assertion.*
import argos.core.conversation.Gaia
import argos.core.conversation.User
import canon.model.Button
import canon.model.Label
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream

class ArgosXMLParsingTest {
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
    val imageXmlFile = File(resourcesPath, "imageAssertionTest.xml")
    val text2speechXmlFile = File(resourcesPath, "text2speechAssertionTest.xml")
    val speech2textXmlFile = File(resourcesPath, "speech2textAssertionTest.xml")
    val semanticSearchXmlFile = File(resourcesPath, "semanticSearchAssertionTest.xml")
    val junitReportXmlFile = File(resourcesPath, "junitReportTest.xml")

    @Test
    fun testIntentAssertions() {
        val parsed = ArgosXML.parse(FileInputStream(intentXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.getAllAssertions()

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
        val assertionList = parsed.getAllAssertions()

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
        val assertionList = parsed.getAllAssertions()

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

                Assertions.assertEquals("steiermark", entities[1].text)
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
        val assertionList = parsed.getAllAssertions()

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
        val assertionList = parsed.getAllAssertions()

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
        val assertionList = parsed.getAllAssertions()

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
    fun testConversationAssertion() {
        val parsed = ArgosXML.parse(FileInputStream(conversationXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.getAllAssertions()

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is ConversationAssertion) {
                Assertions.assertTrue(assertion.spec.participants[0] is Gaia)
                Assertions.assertTrue(assertion.spec.participants[1] is User)
                Assertions.assertTrue(assertion.spec.participants[0] is Gaia)

                Assertions.assertTrue(assertion.spec.participants[0].gatter.renderables[0] is Label)
                Assertions.assertTrue(assertion.spec.participants[1].gatter.renderables[0] is Button)
                Assertions.assertTrue(assertion.spec.participants[2].gatter.renderables[0] is Label)
                Assertions.assertNull((assertion.spec.participants[0].gatter.renderables[0] as Label).id)

                Assertions.assertTrue(assertion.spec.attributes.contains("feature"))
                Assertions.assertEquals("buttons_ordinary_invoke", assertion.spec.attributes.get("feature"))
            }
        }
    }

    @Test
    fun testOCRAssertion() {
        val parsed = ArgosXML.parse(FileInputStream(ocrXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.getAllAssertions()

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
        val assertionList = parsed.getAllAssertions()

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
        val assertionList = parsed.getAllAssertions()

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
        val assertionList = parsed.getAllAssertions()

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is RegressionAssertion) {
                Assertions.assertEquals("Text", assertion.spec.text)
                Assertions.assertEquals(90f, assertion.spec.score)
            }
        }
    }

    @Test
    fun testImageAssertion() {
        val parsed = ArgosXML.parse(FileInputStream(imageXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.getAllAssertions()

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is ImageAssertion) {
                Assertions.assertEquals("image-upscaling", assertion.spec.skill)
                Assertions.assertEquals("url", assertion.spec.source)
                Assertions.assertEquals("url", assertion.spec.target)
            }
        }
    }

    @Test
    fun testText2SpeechAssertion() {
        val parsed = ArgosXML.parse(FileInputStream(text2speechXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.getAllAssertions()

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is Text2SpeechAssertion) {
                Assertions.assertEquals("Text", assertion.spec.text)
                Assertions.assertEquals("url-to-speech", assertion.spec.speech)
            }
        }
    }

    @Test
    fun testSpeech2TextAssertion() {
        val parsed = ArgosXML.parse(FileInputStream(speech2textXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.getAllAssertions()

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is Speech2TextAssertion) {
                Assertions.assertEquals("url-to-speech", assertion.spec.speech)
                Assertions.assertEquals("Text", assertion.spec.text)
            }
        }
    }

    @Test
    fun testSemanticSearchAssertion() {
        val parsed = ArgosXML.parse(FileInputStream(semanticSearchXmlFile))

        val identityId = parsed.identityId
        val assertionList = parsed.getAllAssertions()

        Assertions.assertEquals("16082f40-3043-495a-8833-90fba9d04319", identityId)
        Assertions.assertEquals(1, assertionList.size)

        for (assertion in assertionList) {
            if (assertion is SemanticSearchAssertion) {
                Assertions.assertEquals("", assertion.spec.text)
                Assertions.assertEquals(2, assertion.spec.topN)

                Assertions.assertEquals(2, assertion.spec.entries.size)

                Assertions.assertEquals("document1", assertion.spec.entries[0].id)
                Assertions.assertEquals(0.9f, assertion.spec.entries[0].score)

                Assertions.assertEquals("document2", assertion.spec.entries[1].id)
                Assertions.assertEquals(0.8f, assertion.spec.entries[1].score)
            }
        }
    }

    @Test
    fun testAssertionGroup() {
        val parsed = ArgosXML.parse(FileInputStream(junitReportXmlFile))

        Assertions.assertEquals(4, parsed.getAllAssertions().size)
        Assertions.assertEquals(3, parsed.assertions.size)
    }
}