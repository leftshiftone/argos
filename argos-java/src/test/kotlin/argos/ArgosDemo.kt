package argos

import argos.api.ArgosOptions
import argos.api.Success
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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ArgosDemo {

    // Demo Test

    @Test
    fun `ArgosXML Demo`() {
        val parsedAssertions = ArgosXML.parse(this.javaClass.getResourceAsStream("/nerAssertionTest.xml"))

        val identityId = parsedAssertions.identityId
        val assertions = parsedAssertions.getAllAssertions()

        val options = ArgosOptions(identityId, GaiaConfig("ner-test", HMACCredentials("apiKey", "apiSecret")))

        initMocks(options)

        assertions.forEach { assertion ->
            val result = Flowable.fromPublisher(assertion.assert(options))
            val type = result.blockingFirst()

            Assertions.assertTrue(type is Success)
        }
    }

    @Test
    fun `ArgosDSL Demo`() {
        val options = ArgosOptions("16082f40-3043-495a-8833-90fba9d04319", GaiaConfig("ner-test", HMACCredentials("apiKey", "apiSecret")))

        initMocks(options)

        val results = ArgosDSL.argos("argos-test", options) {
            assertNer("ich suche einen anwalt in der steiermark") {
                entity("location", "steiermark", 6)
                not(entity("organization", "steiermark"))
            }
        }

        Flowable.fromPublisher(results).forEach { type ->
            Assertions.assertTrue(type is Success)
        }

    }

    private fun initMocks(options: ArgosOptions) {
        mockkObject(Gaia)
        val gaiaRef: GaiaRef = mockk()
        every { Gaia.connect(options.config) } returns gaiaRef
        every { gaiaRef.skill(cmpEq("ner-test")).evaluate(any()) } returns Flowable.just(
                SkillEvaluation(mapOf("ner" to listOf(mapOf("text" to "steiermark", "label" to "location")))))
    }
}