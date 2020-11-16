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

class ImageSimilarityAssertionTest: AbstractAssertionTest(
        ImageSimilarityAssertion(ImageSimilarityAssertionSpec("url-to-image", "url-to-image"))) {
    @Test
    fun testSuccess() {
        Assertions.assertEquals(Success::class, testForResponse(mapOf("score" to 0.9f)))
    }

    @Test
    fun testFailure() {
        Assertions.assertEquals(Failure::class, testForResponse(mapOf("score" to 0.8f)))
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