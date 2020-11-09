package argos.core.assertion

import argos.api.Failure
import argos.api.Success
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LanguageDetectionAssertionTest: AbstractAssertionTest(
        LanguageDetectionAssertion(LanguageDetectionAssertionSpec("Das ist ein Beispieltext", "de"))) {

    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(mapOf("lang" to "de")))
    }

    @Test
    fun testFailure() {
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("lang" to "fr")))
    }

}