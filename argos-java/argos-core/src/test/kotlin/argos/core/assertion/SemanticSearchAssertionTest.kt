package argos.core.assertion

import argos.api.Failure
import argos.api.Success
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SemanticSearchAssertionTest: AbstractAssertionTest(
        SemanticSearchAssertion(SemanticSearchAssertionSpec("text", 2, listOf(
                SemanticSearchAssertionSpec.Entry("document1", 0.9f),
                SemanticSearchAssertionSpec.Entry("document2", 0.8f)
        )))
) {
    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(validResponse))
    }

    @Test
    fun testFailure() {
        invalidResponses.forEach {
            Assertions.assertEquals(Failure::class, testForResponse(it, true))
        }
    }

    private val validResponse: Map<String, Any> = mapOf(
            "message" to mapOf(
                    "results" to arrayOf(
                            mapOf("id" to "document1", "score" to 0.9f),
                            mapOf("id" to "document2", "score" to 0.8f))))
    private val invalidResponses = arrayOf(
            mapOf("message" to ""),
            mapOf("message" to mapOf(
                    "results" to "")),
            mapOf("message" to mapOf(
                    "results" to emptyArray<Map<*,*>>())),
            mapOf("message" to mapOf(
                    "results" to arrayOf(
                            mapOf("id" to "document1", "score" to 0.5f),
                            mapOf("id" to "document2", "score" to 0.7f))))
    )
}