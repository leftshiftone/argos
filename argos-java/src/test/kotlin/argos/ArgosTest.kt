package argos

import argos.api.IAssertion
import argos.core.assertion.*
import argos.core.support.ImageSupport
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import gaia.sdk.core.GaiaRef
import io.mockk.*
import io.reactivex.Flowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

class ArgosTest {
    private lateinit var gaiaRef: GaiaRef

    @Test
    fun testXML() {
        testMain(arrayOf("./src/test/resources/intentAssertionTest.xml"), IntentAssertion::class)
    }

    @Test
    fun `test DSL from File`() {
        testMain(arrayOf("./src/test/resources/assertionDSLTest.kts"), IntentAssertion::class)
    }

    @Test
    fun `test DSL with ArgosDSL call and imports`() {
        testMain(arrayOf(
                "import argos.api.ArgosOptions\n" +
                "import argos.runtime.dsl.ArgosDSL\n" +
                "import gaia.sdk.HMACCredentials\n" +
                "import gaia.sdk.core.GaiaConfig\n" +
                "\n" +
                "ArgosDSL.argos(\"argos test\", ArgosOptions(\"\", GaiaConfig(\"\", HMACCredentials(\"\", \"\")))) {\n" +
                "    assertIntent(\"Ich suche einen Anwalt\", \"findLawyer\", 0.9f)\n" +
                "}"), IntentAssertion::class)
    }

    @Test
    fun `test DSL with ArgosDSL call`() {
        testMain(arrayOf(
                "ArgosDSL.argos(\"argos test\", ArgosOptions(\"\", GaiaConfig(\"\", HMACCredentials(\"\", \"\")))) {\n" +
                "    assertIntent(\"Ich suche einen Anwalt\", \"findLawyer\", 0.9f)\n" +
                "}"), IntentAssertion::class)
    }

    @Test
    fun testIntentAssertionDSL() {
        testMain(arrayOf(
                "assertIntent(\"ich suche einen anwalt\",\"findLawyer\",0.9f)"), IntentAssertion::class)
    }

    @Test
    fun testSimilarityAssertionDSL() {
        testMain(arrayOf(
                "assertSimilarity(\"Das ist der erste Text\", \"Das ist der zweite Text\", 0.9f)"),
                SimilarityAssertion::class)
    }

    @Test
    fun testNERAssertionDSL() {
        testMain(arrayOf(
                "assertNer(\"ich suche einen anwalt in der steiermark\") {\n" +
                "        entity(\"location\", \"steiermark\")\n" +
                "        not(entity(\"organization\"))\n" +
                "    }"), NERAssertion::class)
    }

    @Test
    fun testTranslationAssertionDSL() {
        testMain(arrayOf(
                "assertTranslation(\"de\",\"ich suche einen anwalt\",\"en\",\"i am looking for a lawyer\",0.9f)"),
                TranslationAssertion::class)
    }

    @Test
    fun testSentimentAssertionDSL() {
        testMain(arrayOf(
                "assertSentiment(\"ich suche einen anwalt\",\"neutral\")"), SentimentAssertion::class)
    }

    @Test
    fun testImageSimilarityAssertionDSL() {
        testMain(arrayOf(
                "assertImageSimilarity(\"https://via.placeholder.com/150.jpg\", \"https://via.placeholder.com/150.jpg\",0.9f)"),
                ImageSimilarityAssertion::class)
    }

    @Test
    fun testOCRAssertionDSL() {
        testMain(arrayOf(
                "assertOCR(\"https://via.placeholder.com/150.jpg\") {\n" +
                "                text(\"Das ist ein Beispieltext.\", false)\n" +
                "                text(\"Das ist weiterer Beispieltext.\", true)\n" +
                "            }"), OCRAssertion::class)
    }

    @Test
    fun testLanguageDetectionAssertionDSL() {
        testMain(arrayOf(
                "assertLanguageDetection(\"Das ist ein Beispieltext\", \"de\")"), LanguageDetectionAssertion::class)
    }

    @Test
    fun testClassificationAssertionDSL() {
        testMain(arrayOf(
                "assertClassification(\"Text\", \"customClass\")"), ClassificationAssertion::class)
    }

    @Test
    fun testRegressionAssertionDSL() {
        testMain(arrayOf(
                "assertRegression(\"Text\", 90f)"), RegressionAssertion::class)
    }
    @Test
    fun testImageAssertionDSL() {
        testMain(arrayOf(
                "assertImage(\"image-upscaling\", \"https://via.placeholder.com/150.jpg\", \"https://via.placeholder.com/150.jpg\")"),
                ImageAssertion::class)
    }

    private fun testMain(args: Array<String>, assertionType: KClass<out IAssertion>) {
        fun setResponse(map: Map<String, Any>) {
            every { gaiaRef.skill("").evaluate(any()) } returns Flowable.just(SkillEvaluation(map))
        }
        when(assertionType) {
            IntentAssertion::class -> setResponse(mapOf(":type" to "Match", "reference" to "findLawyer"))
            SimilarityAssertion::class -> setResponse(mapOf("score" to 0.9f))
            NERAssertion::class -> setResponse(mapOf("ner" to listOf(mapOf("text" to "steiermark", "label" to "location"))))
            TranslationAssertion::class -> setResponse(mapOf("text" to "i am looking for a lawyer", "lang" to "en"))
            SentimentAssertion::class -> setResponse(mapOf("type" to "neutral"))
            ImageSimilarityAssertion::class -> setResponse(mapOf("score" to 0.9f))
            OCRAssertion::class -> setResponse(mapOf("text" to "Das ist weiterer Beispieltext."))
            LanguageDetectionAssertion::class -> setResponse(mapOf("lang" to "de"))
            ClassificationAssertion::class -> setResponse(mapOf("class" to "customClass"))
            RegressionAssertion::class -> setResponse(mapOf("score" to 90f))
            ImageAssertion::class -> setResponse(mapOf("image" to ImageSupport.getByteArrayFromImage("https://via.placeholder.com/150.jpg")))
        }
        Argos.main(args)
    }

    @BeforeEach
    private fun initMock() {
        mockkObject(Gaia)
        gaiaRef = mockk()
        every { Gaia.connect(ofType()) } returns gaiaRef
    }

    @AfterEach
    private fun verifyMock() {
        verify { Gaia.connect(ofType()) }
        verify { gaiaRef.skill("").evaluate(any()) }
        confirmVerified(gaiaRef)
    }
}
