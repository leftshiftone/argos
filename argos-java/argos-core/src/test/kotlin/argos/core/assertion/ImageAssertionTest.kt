package argos.core.assertion

import argos.api.Failure
import argos.api.Success
import argos.core.support.ImageSupport
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ImageAssertionTest: AbstractAssertionTest(
        ImageAssertion(ImageAssertionSpec("image-upscaling", "url", "url"))) {

    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(mapOf("image" to ByteArray(4))))
    }

    @Test
    fun testFailure() {
        Assertions.assertEquals(Failure::class, testForResponse(mapOf(), true))
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("image" to "url"), true))
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("image" to ByteArray(5)), true))
    }

    @BeforeEach
    fun mockByteArrayFromImage() {
        mockkObject(ImageSupport)
        every { ImageSupport.getByteArrayFromImage(ofType()) } returns ByteArray(4)
    }

    @AfterEach
    fun verifyByteArrayFromImage() {
        verify { ImageSupport.getByteArrayFromImage(ofType()) }
        confirmVerified(ImageSupport)
    }
}