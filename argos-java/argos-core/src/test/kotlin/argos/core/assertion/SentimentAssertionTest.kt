package argos.core.assertion

import argos.api.Failure
import argos.api.Success
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SentimentAssertionTest: AbstractAssertionTest(
        SentimentAssertion(SentimentAssertionSpec("ich suche einen anwalt", "neutral"))) {

    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(mapOf("type" to "neutral")))
    }

    @Test
    fun testFailure() {
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("type" to "positive")))
    }

}