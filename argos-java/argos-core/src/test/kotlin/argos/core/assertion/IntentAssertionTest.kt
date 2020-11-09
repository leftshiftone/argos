package argos.core.assertion

import argos.api.Failure
import argos.api.Success
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class IntentAssertionTest: AbstractAssertionTest(
        IntentAssertion(IntentAssertionSpec("ich suche einen anwalt", "findLawyer"))) {

    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(mapOf(":type" to "Match", "reference" to "findLawyer")))
    }

    @Test
    fun testFailure() {
        Assertions.assertEquals(Failure::class, testForResponse(mapOf(":type" to "no Match")))
    }

}