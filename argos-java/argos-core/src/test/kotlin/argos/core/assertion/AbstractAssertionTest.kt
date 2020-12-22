package argos.core.assertion

import argos.api.*
import argos.core.listener.JUnitReportAssertionListener
import argos.core.listener.LoggingAssertionListener
import gaia.sdk.HMACCredentials
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import gaia.sdk.core.GaiaConfig
import gaia.sdk.core.GaiaRef
import io.mockk.*
import io.reactivex.Flowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import kotlin.reflect.KClass

/**
 * Classes which implement this abstract class can be used to execute argos tests with mocks and predefined responses.
 *
 * @param assertion the assertion on which the mocked argos test should be executed on
 * @param withLogger if <code>true</code>, this test will log infos during the test
 */
abstract class AbstractAssertionTest(val assertion: IAssertion, withLogger: Boolean = false): AbstractArgos() {
    private val options: ArgosOptions = ArgosOptions("")
    private lateinit var gaiaRef: GaiaRef

    init {
        if (withLogger) {
            options.addListener(LoggingAssertionListener())
            options.addListener(JUnitReportAssertionListener())
        }
    }

    /**
     * Executes an argos test for the defined response.
     *
     * @param respondingMap the mocked gaia response
     *
     * @return the type of the result
     */
    fun testForResponse(respondingMap: Map<String, Any>, print: Boolean = false): KClass<out IAssertionResult> {
        setResponse(respondingMap)

        val result = Flowable.fromPublisher(
            argos(assertion::class.java.simpleName, options, listOf(AssertionGroup(null, listOf(assertion))))
        )

        if (print) printResult(result)
        return result.blockingFirst()::class
    }

    @BeforeEach
    private fun initMock() {
        mockkObject(Gaia)
        gaiaRef = mockk()
        every { Gaia.connect(options.config) } returns gaiaRef
    }

    @AfterEach
    private fun verifyMock() {
        verify { Gaia.connect(options.config) }
        verify { gaiaRef.skill("").evaluate(any()) }
        confirmVerified(gaiaRef)
    }

    private fun setResponse(map: Map<String, Any>) {
        every { gaiaRef.skill("").evaluate(any()) } returns Flowable.just(SkillEvaluation(map))
    }

    private fun printResult(result: Flowable<IAssertionResult>) {
        println(" --- Result ---\n" +
                "Type: " + result.blockingFirst()::class + "\n" +
                "Message: " +
                (if (result.blockingFirst().getMessage().contains("\n")) "\n\n" else "")
                    + result.blockingFirst().getMessage())
    }
}