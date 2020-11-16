package argos.core.assertion

import argos.api.Failure
import argos.api.Success
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RegressionAssertionTest: AbstractAssertionTest(
        RegressionAssertion(RegressionAssertionSpec("Text", 90f))) {

    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(mapOf("score" to 90f)))
    }

    @Test
    fun testFailure() {
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("score" to 85f)))
    }
}