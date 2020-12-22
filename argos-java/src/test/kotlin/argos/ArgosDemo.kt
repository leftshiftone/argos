package argos

import argos.api.ArgosOptions
import argos.core.listener.JUnitReportAssertionListener
import argos.core.listener.LoggingAssertionListener
import argos.runtime.dsl.ArgosDSL
import argos.runtime.xml.ArgosXML
import gaia.sdk.HMACCredentials
import gaia.sdk.api.skill.SkillEvaluation
import gaia.sdk.core.Gaia
import gaia.sdk.core.GaiaConfig
import gaia.sdk.core.GaiaRef
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.reactivex.Flowable
import org.junit.jupiter.api.Test

class ArgosDemo {

    // Argos Live Demo

    @Test
    fun `ArgosXML Test Demo`() {
        val input = this.javaClass.getResourceAsStream("/nerAssertionTest.xml")

        val parsedAssertions = ArgosXML.parse(input)

        val options = ArgosOptions(parsedAssertions.identityId, GaiaConfig("ner-test", HMACCredentials("key", "secret")))
        options.addListener(LoggingAssertionListener())
        options.addListener(JUnitReportAssertionListener())

        initMocks(options)

        val resultPub = ArgosXML.argos(parsedAssertions, options)

        Flowable.fromPublisher(resultPub).subscribe()
    }

    @Test
    fun `ArgosDSL Test Demo`() {
        val options = ArgosOptions("16082f40-3043-495a-8833-90fba9d04319", GaiaConfig("ner-test", HMACCredentials("apiKey", "apiSecret")))
        options.addListener(LoggingAssertionListener())
        options.addListener(JUnitReportAssertionListener())

        initMocks(options)

        val results = ArgosDSL.argos("argos-test", options) {
            assertNer("ich suche einen anwalt in der steiermark") {
                entity("location", "steiermark", 6)
                not(entity("organization", "steiermark"))
            }
        }

        Flowable.fromPublisher(results).subscribe()
    }

    @Test
    fun `AssertionGroups DSL Demo`() {
        val options = ArgosOptions("16082f40-3043-495a-8833-90fba9d04319", GaiaConfig("class-test", HMACCredentials("apiKey", "apiSecret")))
        options.addListener(LoggingAssertionListener())
        options.addListener(JUnitReportAssertionListener())

        initMocks(options)

        val results = ArgosDSL.argos("ClassificationAssertionTest", options) {
            assertionGroup("Classification Test#1") {
                assertClassification("Text", "customClass")
                assertClassification("Text", "anotherClass")
            }
            assertClassification("Text", "otherClass")
            assertClassification("Text", "someOtherClass")
        }

        Flowable.fromPublisher(results).subscribe()
    }

    // ---- Mocks Initialisation ----
    private fun initMocks(options: ArgosOptions) {
        mockkObject(Gaia)
        val gaiaRef: GaiaRef = mockk()
        every { Gaia.connect(options.config) } returns gaiaRef
        every { gaiaRef.skill(cmpEq("ner-test")).evaluate(any()) } returns Flowable.just(
                SkillEvaluation(mapOf("ner" to listOf(mapOf("text" to "steiermark", "label" to "location")))))
        every { gaiaRef.skill(cmpEq("class-test")).evaluate(any()) } returns Flowable.just(
            SkillEvaluation(mapOf("class" to "customClass")))
    }
}