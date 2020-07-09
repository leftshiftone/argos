package argos.runtime.xml

import argos.api.ArgosOptions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.assertj.core.api.Assertions.assertThat

@Tag("unitTest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArgosIntentXMLTest {

    private val argosOptions = ArgosOptions("75a7b711-aa64-37e3-bbd2-6cf72cf2e19a",
    "heimdall", "secret", "wss://mqtt.beta.gaia.leftshift.one/mqtt")

    @Test
    fun `test XML intent`() {
        val result = ArgosXML.argos("argos xml test", argosOptions) {
            loadFlatFormat("simpleIntent.xml", "assertions").forEach {
                assertIntent(it)
            }
        }

        assertThat(result).isNotNull()
    }
 }