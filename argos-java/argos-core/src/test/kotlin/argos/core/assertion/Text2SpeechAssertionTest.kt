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

class Text2SpeechAssertionTest: AbstractAssertionTest(
        Text2SpeechAssertion(Text2SpeechAssertionSpec("Text", "url"))) {

    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(mapOf("speech" to ByteArray(4))))
    }

    @Test
    fun testFailure() {
        Assertions.assertEquals(Failure::class, testForResponse(mapOf(), true))
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("speech" to "url"), true))
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("speech" to ByteArray(5)), true))
    }

    @BeforeEach
    fun mockByteArrayFromFile() {
        mockkObject(FileSupport)
        every { FileSupport.getByteArrayFromFile(ofType()) } returns ByteArray(4)
    }

    @AfterEach
    fun verifyByteArrayFromFile() {
        verify { FileSupport.getByteArrayFromFile(ofType()) }
        confirmVerified(FileSupport)
    }
}