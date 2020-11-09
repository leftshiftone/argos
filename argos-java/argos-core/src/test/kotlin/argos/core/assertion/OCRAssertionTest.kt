package argos.core.assertion

import argos.api.Failure
import argos.api.Success
import argos.core.assertion.support.ImageSupport
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OCRAssertionTest: AbstractAssertionTest(OCRAssertion(OCRAssertionSpec("url-to-image", listOf(
        OCRAssertionSpec.Text("Das ist ein Beispieltext.", false),
        OCRAssertionSpec.Text("Das ist weiterer Beispieltext.", true))))) {

    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(mapOf("text" to "Das ist weiterer Beispieltext.")))
    }

    @Test
    fun testFailure() {
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("text" to "Das ist ein Beispieltext.")))
    }

    @BeforeEach
    fun mockByteArrayFromImage() {
        mockkObject(ImageSupport)
        every { ImageSupport.getByteArrayFromImage("url-to-image") } returns ByteArray(4)
    }

    @AfterEach
    fun verifyByteArrayFromImage() {
        verify { ImageSupport.getByteArrayFromImage("url-to-image") }
        confirmVerified(ImageSupport)
    }
}