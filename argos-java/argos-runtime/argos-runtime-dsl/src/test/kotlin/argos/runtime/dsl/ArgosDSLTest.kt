package argos.runtime.dsl

import argos.api.*
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
import org.reactivestreams.Publisher

class ArgosDSLTest {
    private val options = ArgosOptions("", GaiaConfig("", HMACCredentials("", "")))
    private lateinit var gaiaRef: GaiaRef

    @Test
    fun testIntent() {
        every { gaiaRef.skill("").evaluate(any()) } returns Flowable.just(
                SkillEvaluation(mapOf(":type" to "Match", "reference" to "findLawyer")))

        val result = ArgosDSL.argos("argos test", options) {
            assertIntent("ich suche einen anwalt", "findLawyer")
        }
        val type = Flowable.fromPublisher(result).blockingFirst()

        Assertions.assertTrue(type is Success)
    }

    @Test
    fun testSimilarity() {
        every { gaiaRef.skill("").evaluate(any()) } returns Flowable.just(
                SkillEvaluation(mapOf("score" to 0.9f)))

        val result = ArgosDSL.argos("argos test", options) {
            assertSimilarity("Der erste Text", "Der zweite Text", 0.9f)
        }
        val type = Flowable.fromPublisher(result).blockingFirst()

        Assertions.assertTrue(type is Success)
    }

    @Test
    fun testNER() {
        every { gaiaRef.skill("").evaluate(any()) } returns Flowable.just(
                SkillEvaluation(mapOf("ner" to listOf(mapOf("text" to "steiermark", "label" to "location")))))

        val result = ArgosDSL.argos("argos test", options) {
            assertNer("ich suche einen anwalt in der steiermark") {
                entity("location", "steiermark", 6)
                not(entity("organization"))
            }
        }
        val type = Flowable.fromPublisher(result).blockingFirst()

        Assertions.assertTrue(type is Success)
    }

    @BeforeEach
    fun initMock() {
        mockkObject(ArgosDSL)
        mockkObject(Gaia)
        gaiaRef = mockk()

        every { Gaia.connect(options.config) } returns gaiaRef
    }

    @AfterEach
    fun verifyMock() {
        verify { Gaia.connect(options.config) }
        verify { gaiaRef.skill("").evaluate(any()) }
        verify { ArgosDSL.argos("argos test", options, any()) }

        confirmVerified(ArgosDSL)
        confirmVerified(gaiaRef)
    }
}