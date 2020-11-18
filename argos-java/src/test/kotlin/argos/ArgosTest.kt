package argos

import argos.api.IAssertion
import argos.core.assertion.*
import argos.core.support.FileSupport
import argos.core.support.ImageSupport
import com.sun.org.apache.xpath.internal.Arg
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
    private val testArgs = arrayOf("url=test_url", "key=test_key", "secret=test_secret")

    @Test
    fun testXML() {
        testMain(arrayOf(*testArgs, "./src/test/resources/intentAssertionTest.xml"), IntentAssertion::class)
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
                "ArgosDSL.argos(\"argos test\", ArgosOptions(\"test_id\", GaiaConfig(\"test_url\", HMACCredentials(\"test_key\", \"test_secret\")))) {\n" +
                "    assertIntent(\"Ich suche einen Anwalt\", \"findLawyer\", 0.9f)\n" +
                "}"), IntentAssertion::class)
    }

    @Test
    fun `test DSL with ArgosDSL call`() {
        testMain(arrayOf(
                "ArgosDSL.argos(\"argos test\", ArgosOptions(\"test_id\", GaiaConfig(\"test_url\", HMACCredentials(\"test_key\", \"test_secret\")))) {\n" +
                "    assertIntent(\"Ich suche einen Anwalt\", \"findLawyer\", 0.9f)\n" +
                "}"), IntentAssertion::class)
    }

    @Test
    fun testIntentAssertionDSL() {
        testMain(arrayOf(*testArgs,
                "assertIntent(\"ich suche einen anwalt\",\"findLawyer\",0.9f)"), IntentAssertion::class)
    }

    @Test
    fun testSimilarityAssertionDSL() {
        testMain(arrayOf(*testArgs,
                "assertSimilarity(\"Das ist der erste Text\", \"Das ist der zweite Text\", 0.9f)"),
                SimilarityAssertion::class)
    }

    @Test
    fun testNERAssertionDSL() {
        testMain(arrayOf(*testArgs,
                "assertNer(\"ich suche einen anwalt in der steiermark\") {\n" +
                "        entity(\"location\", \"steiermark\")\n" +
                "        not(entity(\"organization\"))\n" +
                "    }"), NERAssertion::class)
    }

    @Test
    fun testTranslationAssertionDSL() {
        testMain(arrayOf(*testArgs,
                "assertTranslation(\"de\",\"ich suche einen anwalt\",\"en\",\"i am looking for a lawyer\",0.9f)"),
                TranslationAssertion::class)
    }

    @Test
    fun testSentimentAssertionDSL() {
        testMain(arrayOf(*testArgs,
                "assertSentiment(\"ich suche einen anwalt\",\"neutral\")"), SentimentAssertion::class)
    }

    @Test
    fun testImageSimilarityAssertionDSL() {
        testMain(arrayOf(*testArgs,
                "assertImageSimilarity(\"https://via.placeholder.com/150.jpg\", \"https://via.placeholder.com/150.jpg\",0.9f)"),
                ImageSimilarityAssertion::class)
    }

    @Test
    fun testOCRAssertionDSL() {
        testMain(arrayOf(*testArgs,
                "assertOCR(\"https://via.placeholder.com/150.jpg\") {\n" +
                "                text(\"Das ist ein Beispieltext.\", false)\n" +
                "                text(\"Das ist weiterer Beispieltext.\", true)\n" +
                "            }"), OCRAssertion::class)
    }

    @Test
    fun testLanguageDetectionAssertionDSL() {
        testMain(arrayOf(*testArgs,
                "assertLanguageDetection(\"Das ist ein Beispieltext\", \"de\")"), LanguageDetectionAssertion::class)
    }

    @Test
    fun testClassificationAssertionDSL() {
        testMain(arrayOf(*testArgs,
                "assertClassification(\"Text\", \"customClass\")"), ClassificationAssertion::class)
    }

    @Test
    fun testRegressionAssertionDSL() {
        testMain(arrayOf(*testArgs,
                "assertRegression(\"Text\", 90f)"), RegressionAssertion::class)
    }
    @Test
    fun testImageAssertionDSL() {
        testMain(arrayOf(*testArgs,
                "assertImage(\"image-upscaling\", \"https://via.placeholder.com/150.jpg\", \"https://via.placeholder.com/150.jpg\")"),
                ImageAssertion::class)
    }

    @Test
    fun testText2Speech() {
        testMain(arrayOf(*testArgs,
                "assertText2Speech(\"Test\", \"example.wav\")"), Text2SpeechAssertion::class)
    }

    private fun testMain(args: Array<String>, assertionType: KClass<out IAssertion>) {
        fun setResponse(map: Map<String, Any>) {
            every { gaiaRef.skill("test_url").evaluate(any()) } returns Flowable.just(SkillEvaluation(map))
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
            Text2SpeechAssertion::class -> setResponse(mapOf("speech" to ByteArray(4)))
        }
        println("Args: ${args.asList()}")
        Argos.main(args)
    }

    @BeforeEach
    private fun initMock() {
        mockkObject(Gaia)
        gaiaRef = mockk()
        mockkObject(FileSupport)
        every { Gaia.connect(ofType()) } returns gaiaRef
        every { FileSupport.getByteArrayFromFile(ofType()) } returns ByteArray(4)
    }

    @AfterEach
    private fun verifyMock() {
        verify { Gaia.connect(ofType()) }
        verify { gaiaRef.skill("test_url").evaluate(any()) }
        confirmVerified(gaiaRef)
    }
}
