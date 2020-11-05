package argos.core.assertion

import argos.api.ArgosOptions
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

class LanguageDetectionAssertionTest {
    private val spec = LanguageDetectionAssertionSpec("Das ist ein Beispieltext", "de")
    private val options = ArgosOptions("", GaiaConfig("", HMACCredentials("", "")))
    private lateinit var gaiaRef: GaiaRef
    private lateinit var skillEval: SkillEvaluation

    @Test
    fun testSuccess() {
        initResultType(Success(""))

        val result = Flowable.fromPublisher(LanguageDetectionAssertion(spec).assert(options))

        Assertions.assertFalse(result.isEmpty.blockingGet())
        Assertions.assertTrue(result.blockingFirst() is Success)

        printResult(result)
    }

    @Test
    fun testFailure() {
        initResultType(Failure(""))

        val result = Flowable.fromPublisher(LanguageDetectionAssertion(spec).assert(options))

        Assertions.assertFalse(result.isEmpty.blockingGet())
        Assertions.assertTrue(result.blockingFirst() is Failure)

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
            is Success -> skillEval = SkillEvaluation(mapOf("lang" to "de"))
            is Failure -> skillEval = SkillEvaluation(mapOf("lang" to "fr"))
        }
        every { gaiaRef.skill("").evaluate(any()) } returns Flowable.just(skillEval)
    }

    private fun printResult(result: Flowable<IAssertionResult>) {
        println(" --- Result ---\n" +
                "Type: " + result.blockingFirst()::class + "\n" +
                "Message: " + result.blockingFirst().getMessage())
    }
}