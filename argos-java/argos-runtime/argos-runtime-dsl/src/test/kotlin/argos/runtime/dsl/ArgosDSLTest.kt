package argos.runtime.dsl

import argos.api.ArgosOptions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import arrow.core.success
import io.mockk.mockk

@Tag("unitTest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArgosDSLTest {

    private val argosOptions = mockk<ArgosOptions>()

    @Test
    fun `test DSL intent`() {

        ArgosDSL.argos("argos test", argosOptions) {
            assertIntent("ich suche einen anwalt", "findLayer")
        }.success()
    }


    @Test
    fun `test DSL augmentation`() {

        ArgosDSL.argos("argos test", argosOptions) {
            assertIntent(qwertzAugmentation("ich suche einen anwalt"), "findLayer")
        }.success()
    }
}


