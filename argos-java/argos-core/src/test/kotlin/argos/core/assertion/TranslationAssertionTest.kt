package argos.core.assertion

import argos.api.Failure
import argos.api.Success
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TranslationAssertionTest: AbstractAssertionTest(
        TranslationAssertion(TranslationAssertionSpec("de","ich suche einen anwalt",
                "en", "i am looking for a lawyer", 0.9f))) {

    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(mapOf(
                "text" to "i am looking for a lawyer",
                "lang" to "en")))
    }

    @Test
    fun testFailure() {
        Assertions.assertEquals(Failure::class, testForResponse(mapOf(
                "text" to "je recherche un avocat",
                "lang" to "fr")))
    }

}