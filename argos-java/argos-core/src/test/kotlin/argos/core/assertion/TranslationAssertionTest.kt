package argos.core.assertion

import argos.api.ArgosOptions
import argos.api.Error
import argos.api.Failure
import argos.api.IAssertionResult
import argos.api.Success
import gaia.sdk.HMACCredentials
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import gaia.sdk.core.GaiaConfig
import gaia.sdk.core.GaiaRef
import io.mockk.*
import io.reactivex.Flowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TranslationAssertionTest {
    private val spec = TranslationAssertionSpec("de","ich suche einen anwalt",
            "en", "i am looking for a lawyer", 0.9f)
    private val options = ArgosOptions("", GaiaConfig("", HMACCredentials("", "")))
    private lateinit var gaiaRef: GaiaRef
    private lateinit var skillEval: SkillEvaluation

    @Test
    fun testSuccess() {
        initResultType(Success(""))

        val result = Flowable.fromPublisher(TranslationAssertion(spec).assert(options))

        Assertions.assertFalse(result.isEmpty.blockingGet())
        Assertions.assertTrue(result.blockingFirst() is Success)

        printResult(result)
    }

    @Test
    fun testFailure() {
        initResultType(Failure(""))

        val result = Flowable.fromPublisher(TranslationAssertion(spec).assert(options))

        Assertions.assertFalse(result.isEmpty.blockingGet())
        Assertions.assertTrue(result.blockingFirst() is Failure)

        printResult(result)
    }

    @Test
    fun testError() {
        initResultType(Error(Throwable()))

        val result = Flowable.fromPublisher(TranslationAssertion(spec).assert(options))

        Assertions.assertFalse(result.isEmpty.blockingGet())
        Assertions.assertTrue(result.blockingFirst() is Error)

        printResult(result)
    }

    @BeforeEach
    fun initMock() {
        mockkObject(Gaia)
        gaiaRef = mockk()

        every { Gaia.connect(options.config) } returns gaiaRef
    }

    @AfterEach
    fun verifyMock() {
        verify { Gaia.connect(options.config) }
        verify { gaiaRef.skill("").evaluate(any()) }
        confirmVerified(gaiaRef)
    }

    private fun initResultType(type: IAssertionResult) {
        when(type) {
            is Success -> skillEval = SkillEvaluation(
                    mapOf("text" to spec.translatedText, "lang" to spec.translationLang))
            is Failure -> skillEval = SkillEvaluation(
                    mapOf("text" to spec.translatedText, "lang" to "fr"))
            is Error -> skillEval = SkillEvaluation(mapOf("abc" to "def"))
        }
        every { gaiaRef.skill("").evaluate(any()) } returns Flowable.just(skillEval)
    }

    private fun printResult(result: Flowable<IAssertionResult>) {
        println(" --- Result ---\n" +
                "Type: " + result.blockingFirst()::class + "\n" +
                "Message: " + result.blockingFirst().getMessage())
    }
}