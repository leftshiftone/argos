package argos

import argos.api.IAssertion
import argos.core.assertion.*
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

    @Test fun testXML() {
        testMain(arrayOf("./src/test/resources/intentAssertionTest.xml"), IntentAssertion::class)
    }

    @Test fun `test DSL from File`() {
        testMain(arrayOf("./src/test/resources/assertionDSLTest.kts"), IntentAssertion::class)
    }
    @Test fun `test DSL with ArgosDSL call and imports`() {
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
    @Test fun `test DSL with ArgosDSL call`() {
        testMain(arrayOf(
                "ArgosDSL.argos(\"argos test\", ArgosOptions(\"\", GaiaConfig(\"\", HMACCredentials(\"\", \"\")))) {\n" +
                "    assertIntent(\"Ich suche einen Anwalt\", \"findLawyer\", 0.9f)\n" +
                "}"), IntentAssertion::class)
    }

    @Test fun testIntentAssertionDSL() {
        testMain(arrayOf(
                "assertIntent(\"ich suche einen anwalt\",\"findLawyer\",0.9f)"), IntentAssertion::class)
    }
    @Test fun testSimilarityAssertionDSL() {
        testMain(arrayOf(
                "assertSimilarity(\"Das ist der erste Text\", \"Das ist der zweite Text\", 0.9f)"),
                SimilarityAssertion::class)
    }
    @Test fun testNERAssertionDSL() {
        testMain(arrayOf(
                "assertNer(\"ich suche einen anwalt in der steiermark\") {\n" +
                "        entity(\"location\", \"steiermark\")\n" +
                "        not(entity(\"organization\"))\n" +
                "    }"), NERAssertion::class)
    }
    @Test fun testTranslationAssertionDSL() {
        testMain(arrayOf(
                "assertTranslation(\"de\",\"ich suche einen anwalt\",\"en\",\"i am looking for a lawyer\",0.9f)"),
                TranslationAssertion::class)
    }
    @Test fun testSentimentAssertionDSL() {
        testMain(arrayOf(
                "assertSentiment(\"ich suche einen anwalt\",\"neutral\")"), SentimentAssertion::class)
    }
    @Test fun testClassificationAssertionDSL() {
        testMain(arrayOf(
                "assertClassification(\"Text\", \"customClass\")"), ClassificationAssertion::class)
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
            ClassificationAssertion::class -> setResponse(mapOf("class" to "customClass"))
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
