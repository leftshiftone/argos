package argos.core.assertion

import argos.api.Failure
import argos.api.Success
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ClassificationAssertionTest : AbstractAssertionTest(
        ClassificationAssertion(ClassificationAssertionSpec("Text", "customClass"))) {

    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(mapOf("class" to "customClass")))
    }

    @Test
    fun testFailure() {
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("class" to "somethingElse")))
    }
}