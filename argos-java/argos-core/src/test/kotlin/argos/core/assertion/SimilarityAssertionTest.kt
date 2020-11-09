package argos.core.assertion

import argos.api.Failure
import argos.api.Success
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SimilarityAssertionTest: AbstractAssertionTest(
        SimilarityAssertion(SimilarityAssertionSpec("Der erste Text", "Der zweite Text", 0.9f))) {

    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(mapOf("score" to 0.9f)))
    }

    @Test
    fun testFailure() {
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("score" to 0.8f)))
    }

}