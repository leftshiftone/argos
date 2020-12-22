package argos.runtime.xml

import argos.api.ArgosOptions
import argos.core.assertion.AbstractArgos
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
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream

class ArgosXMLTest: AbstractArgos() {
    private val resourcesPath = File("./src/test/resources/")
    private val junitReportXmlFile = File(resourcesPath, "junitReportTest.xml")
    private lateinit var gaiaRef: GaiaRef

    @Test
    fun testArgos() {
        val parsed = ArgosXML.parse(FileInputStream(junitReportXmlFile))
        Flowable.fromPublisher(ArgosXML.argos(parsed)).subscribe()
    }

    @Test
    fun testArgosWithReport() {
        val parsed = ArgosXML.parse(FileInputStream(junitReportXmlFile))
        val options = ArgosOptions(parsed.identityId)
        options.addListener(LoggingAssertionListener())
        options.addListener(JUnitReportAssertionListener())
        Flowable.fromPublisher(ArgosXML.argos(parsed, options)).subscribe()
    }

    @BeforeEach
    private fun initMock() {
        mockkObject(Gaia)
        gaiaRef = mockk()
        every { Gaia.connect(any()) } returns gaiaRef
        every { gaiaRef.skill("").evaluate(any()) } returns Flowable.just(
            SkillEvaluation(mapOf("class" to "customClass")))
    }

    @AfterEach
    private fun verifyMock() {
        verify { Gaia.connect(any()) }
        verify { gaiaRef.skill("").evaluate(any()) }
        confirmVerified(gaiaRef)
    }
}