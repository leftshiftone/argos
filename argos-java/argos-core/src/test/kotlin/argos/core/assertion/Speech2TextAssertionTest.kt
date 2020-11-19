package argos.core.assertion

import argos.api.Failure
import argos.api.Success
import argos.core.support.FileSupport
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Speech2TextAssertionTest : AbstractAssertionTest(
        Speech2TextAssertion(Speech2TextAssertionSpec("url-to-speech", "Text"))) {

    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(mapOf("text" to "Text")))
    }

    @Test
    fun testFailure() {
        Assertions.assertEquals(Failure::class, testForResponse(mapOf(), true))
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("text" to ByteArray(4)), true))
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("text" to "no Text"), true))
    }

    @BeforeEach
    fun mockByteArrayFromFile() {
        mockkObject(FileSupport)
        every { FileSupport.getByteArrayFromFile("url-to-speech") } returns ByteArray(4)
    }

    @AfterEach
    fun verifyByteArrayFromFile() {
        verify { FileSupport.getByteArrayFromFile(ofType()) }
        confirmVerified(FileSupport)
    }
}