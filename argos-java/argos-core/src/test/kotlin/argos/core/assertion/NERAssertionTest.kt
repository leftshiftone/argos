package argos.core.assertion

import argos.api.Failure
import argos.api.Success
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NERAssertionTest: AbstractAssertionTest(
        NERAssertion(NERAssertionSpec("ich suche einen anwalt in der steiermark", listOf(
                NERAssertionSpec.Entity(label = "location", text = "steiermark", index = 6),
                NERAssertionSpec.Entity(label = "organization", text = "", not = true))))) {

    private val validResponse: List<Map<String, Any>> = listOf(mapOf("text" to "steiermark", "label" to "location"))
    private val invalidResponse: List<Map<String, Any>> = listOf(mapOf("text" to "steiermark", "label" to "organization"))

    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(mapOf("ner" to validResponse)))
    }

    @Test
    fun testFailure() {
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("ner" to invalidResponse)))
    }

}